package com.example.myflower.service.impl;

import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.dto.order.requests.*;
import com.example.myflower.dto.order.responses.CountOrderStatusResponseDTO;
import com.example.myflower.dto.order.responses.OrderResponseDTO;
import com.example.myflower.dto.order.responses.OrderDetailResponseDTO;
import com.example.myflower.dto.order.responses.ReportResponseDTO;
import com.example.myflower.entity.*;
import com.example.myflower.entity.enumType.*;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.order.OrderAppException;
import com.example.myflower.mapper.FlowerListingMapper;
import com.example.myflower.repository.*;
import com.example.myflower.service.*;
import com.example.myflower.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderSummaryRepository orderSummaryRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private FlowerListingRepository flowerListingRepository;

    @Autowired
    private FlowerListingService flowerListingService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private KafkaTemplate<String, OrderResponseDTO> kafkaTemplate;

    @Override
    @Transactional
    public OrderResponseDTO orderByWallet(CreateOrderRequestDTO orderDTO) throws OrderAppException {
        if (orderDTO.getPaymentMethod() != PaymentMethodEnum.WALLET){
            throw new OrderAppException(ErrorCode.ORDER_INVALID);
        }
        // Get the current user account
        Account account = AccountUtils.getCurrentAccount();
        if (account == null) {
            throw new OrderAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        Map<Account, BigDecimal> sellerBalanceMap = new HashMap<>();

        BigDecimal totalPrice = orderDTO.getOrderDetails().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (account.getBalance() == null || account.getBalance().compareTo(totalPrice) < 0) {
            throw new OrderAppException(ErrorCode.ORDER_INVALID_FUNDS);
        }

        // Create order summary
        OrderSummary orderSummary = createOrderFromDTO(orderDTO, account, totalPrice);
        orderSummaryRepository.save(orderSummary);


        // Create order details
        List<OrderDetail> orderDetails = createOrderDetails(orderDTO, orderSummary, account, sellerBalanceMap);

        distributeBalance(account, totalPrice, orderSummary, sellerBalanceMap);

        List<OrderDetailResponseDTO> orderDetailsResponseDTO = convertOrderDetailDTO(orderDetails);
        // Return response with order details
        String message = "Order by wallet successfully";
        OrderResponseDTO orderResponseDTO = createOrderByWalletResponseDTO(orderSummary, account, orderDetailsResponseDTO, message);
        kafkaTemplate.send("email_order_wallet_topic", orderResponseDTO);
        return orderResponseDTO;
    }

    @Override
    @Transactional
    public OrderResponseDTO orderByCod(CreateOrderRequestDTO orderDTO) throws OrderAppException {
        if (orderDTO.getPaymentMethod() != PaymentMethodEnum.COD){
            throw new OrderAppException(ErrorCode.ORDER_INVALID);
        }
        // Get the current user account
        Account account = AccountUtils.getCurrentAccount();
        if (account == null) {
            throw new OrderAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        Map<Account, BigDecimal> sellerBalanceMap = new HashMap<>();

        BigDecimal totalPrice = orderDTO.getOrderDetails().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order summary
        OrderSummary orderSummary = createOrderFromDTO(orderDTO, account, totalPrice);
        orderSummaryRepository.save(orderSummary);


        // Create order details
        List<OrderDetail> orderDetails = createOrderDetails(orderDTO, orderSummary, account, sellerBalanceMap);

        List<OrderDetailResponseDTO> orderDetailsResponseDTO = convertOrderDetailDTO(orderDetails);
        // Return response with order details
        String message = "Order by cod successfully";
        OrderResponseDTO orderResponseDTO = createOrderByWalletResponseDTO(orderSummary, account, orderDetailsResponseDTO, message);
        kafkaTemplate.send("email_order_wallet_topic", orderResponseDTO);
        return orderResponseDTO;
    }

    private void distributeBalance(Account accountBuyer, BigDecimal totalPrice, OrderSummary orderSummary, Map<Account, BigDecimal> sellerBalanceMap) {
        Account accountAdmin = adminService.getAccountAdmin();
        // subtract balance for buyer
        accountService.handleBalanceByOrder(accountBuyer, totalPrice, WalletLogTypeEnum.SUBTRACT, WalletLogActorEnum.BUYER, orderSummary, null, WalletLogStatusEnum.SUCCESS, false);
        for (Map.Entry<Account, BigDecimal> entry : sellerBalanceMap.entrySet()) {
            Account accountSeller = entry.getKey();
            BigDecimal amountInitial = entry.getValue();
            // handle calculator
            BigDecimal calculatorAmountForSeller = amountInitial.multiply(BigDecimal.valueOf(1).subtract(accountAdmin.getFeeService()));
            // add balance for seller
            accountService.handleBalanceByOrder(accountSeller, calculatorAmountForSeller, WalletLogTypeEnum.ADD, WalletLogActorEnum.SELLER, orderSummary, null, WalletLogStatusEnum.SUCCESS, false);
        }

        // add balance for admin
        accountService.handleBalanceByOrder(accountAdmin, totalPrice.multiply(accountAdmin.getFeeService()), WalletLogTypeEnum.ADD, WalletLogActorEnum.ADMIN ,orderSummary, null, WalletLogStatusEnum.SUCCESS, false);
        sellerBalanceMap.clear();
    }

    private OrderSummary createOrderFromDTO(CreateOrderRequestDTO orderDTO, Account account, BigDecimal totalPrice) {

        return OrderSummary.builder()
                .user(account)
                .buyerName(orderDTO.getBuyerName())
                .buyerAddress(orderDTO.getBuyerAddress())
                .buyerPhone(orderDTO.getBuyerPhone())
                .buyerEmail(account.getEmail())
                .totalPrice(totalPrice)
                .note(orderDTO.getNote())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<OrderDetail> createOrderDetails(CreateOrderRequestDTO orderDTO, OrderSummary orderSummary, Account account, Map<Account, BigDecimal> sellerBalanceMap) throws OrderAppException {
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailRequestDTO item : orderDTO.getOrderDetails()) {

            FlowerListing flowerListing = flowerListingRepository.findByIdWithLock(item.getFlowerListingId());
            if (flowerListing.getStockQuantity().compareTo(item.getQuantity()) < 0) {
                throw new OrderAppException(ErrorCode.FLOWER_OUT_OF_STOCK);
            }
            Account seller = flowerListing.getUser();
            sellerBalanceMap.merge(seller, item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())), BigDecimal::add);
            flowerListingService.updateQuantityFlowerListing(flowerListing, item.getQuantity());
            OrderDetail orderDetail = OrderDetail.builder()
                    .orderSummary(orderSummary)
                    .seller(flowerListing.getUser())
                    .flowerListing(flowerListing)
                    .price(item.getPrice())
                    .paymentMethod(orderDTO.getPaymentMethod())
                    .quantity(item.getQuantity())
                    .status(OrderDetailsStatusEnum.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            OrderDetail response = orderDetailRepository.save(orderDetail);
            orderDetails.add(response);
        }
        return orderDetails;
    }

    private OrderResponseDTO createOrderByWalletResponseDTO(OrderSummary orderSummary, Account account, List<OrderDetailResponseDTO> orderDetailsResponseDTO, String message) {
        return OrderResponseDTO.builder()
                .message(message)
                .error(false)
                .id(orderSummary.getId())
                .buyerName(orderSummary.getBuyerName())
                .buyerAddress(orderSummary.getBuyerAddress())
                .buyerPhone(orderSummary.getBuyerPhone())
                .buyerEmail(account.getEmail())
                .totalAmount(orderSummary.getTotalPrice())
                .balance(account.getBalance())
                .note(orderSummary.getNote())
                .orderDetails(orderDetailsResponseDTO)
                .createdAt(orderSummary.getCreatedAt())
                .build();
    }

    private List<OrderDetailResponseDTO> convertOrderDetailDTO(List<OrderDetail> orderDetails) {
        return orderDetails.stream().map(
                this::convertOrderDetailDTO
        ).toList();
    }
    private AccountResponseDTO convertAccountDTO(Account account) {
        return AccountResponseDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .phone(account.getPhone())
                .build();
    }
    private OrderDetailResponseDTO convertOrderDetailDTO(OrderDetail orderDetail) {
        FlowerListingResponseDTO flowerListingResponseDTO = FlowerListingResponseDTO.builder()
                .id(orderDetail.getId())
                .name(orderDetail.getFlowerListing().getName())
                .user(convertAccountDTO(orderDetail.getFlowerListing().getUser()))
                .images(Collections.singletonList(flowerListingService.getFeaturedFlowerImage(orderDetail.getFlowerListing().getId())))
                .description(orderDetail.getFlowerListing().getDescription())
                .build();
        return OrderDetailResponseDTO.builder()
                .id(orderDetail.getId())
                .flowerListing(flowerListingResponseDTO)
                .paymentMethod(orderDetail.getPaymentMethod())
                .status(orderDetail.getStatus())
                .price(orderDetail.getPrice())
                .quantity(orderDetail.getQuantity())
                .createAt(orderDetail.getCreatedAt())
                .build();
    }

    public List<OrderDetailResponseDTO> getAllOrderDetailsByOrderSummaryId(Integer orderSummaryId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findOrderDetailByOrderSummaryId(orderSummaryId);
        return convertOrderDetailDTO(orderDetails);
    }

    @Override
    public Page<OrderDetailResponseDTO> getAllOrderByBuyer(GetOrderDetailsRequestDTO requestDTO) {
        Account account = AccountUtils.getCurrentAccount();
        if (account == null || account.getBalance() == null) {
            throw new OrderAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        List<OrderDetailsStatusEnum> defaultStatusList = List.of(
                OrderDetailsStatusEnum.PENDING,
                OrderDetailsStatusEnum.SELLER_CANCELED,
                OrderDetailsStatusEnum.BUYER_CANCELED,
                OrderDetailsStatusEnum.DELIVERED,
                OrderDetailsStatusEnum.SHIPPED,
                OrderDetailsStatusEnum.PREPARING
        );

        if (requestDTO.getStatus() == null || requestDTO.getStatus().isEmpty()) {
            requestDTO.setStatus(defaultStatusList);
        }

        if(requestDTO.getStartDate() == null ){
            requestDTO.setStartDate(LocalDate.of(1970, 1, 1));
        }

        if(requestDTO.getEndDate() == null ){
            requestDTO.setEndDate(LocalDate.of(9999, 12, 31));
        }
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(requestDTO.getOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(requestDTO.getPageNumber(), requestDTO.getPageSize(), Sort.by(sortDirection, requestDTO.getSortBy()));

        Page<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderSummary_UserAndStatusInAndCreatedAtBetweenAndSearch(
                account,
                requestDTO.getStatus(),
                requestDTO.getStartDate().atStartOfDay(),
                requestDTO.getEndDate().plusDays(1).atStartOfDay(),
                "%" + requestDTO.getSearch() + "%",
                pageable);

        return  orderDetails.map(
                orderDetail -> {
                    OrderDetailResponseDTO responseDTO = OrderDetailResponseDTO.builder()
                            .id(orderDetail.getId())
                            .price(orderDetail.getPrice())
                            .quantity(orderDetail.getQuantity())
                            .flowerListing(FlowerListingMapper.toFlowerListingResponseDTO(orderDetail.getFlowerListing()))
                            .orderSummary(buildOrderResponseDTO(orderDetail.getOrderSummary()))
                            .createAt(orderDetail.getCreatedAt())
                            .status(orderDetail.getStatus())
                            .build();
                            FlowerListingResponseDTO flowerListingResponseDTO = responseDTO.getFlowerListing();
                            flowerListingResponseDTO.setImages(Collections.singletonList(flowerListingService.getFeaturedFlowerImage(orderDetail.getId())));
                            return responseDTO;
                }
        );
    }

    @Override
    public Page<OrderDetailResponseDTO> getOrdersBySeller(GetOrderDetailsRequestDTO requestDTO) {
        Account account = AccountUtils.getCurrentAccount();
        if (account == null || account.getBalance() == null) {
            throw new OrderAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        List<OrderDetailsStatusEnum> defaultStatusList = List.of(
                OrderDetailsStatusEnum.PENDING,
                OrderDetailsStatusEnum.SELLER_CANCELED,
                OrderDetailsStatusEnum.BUYER_CANCELED,
                OrderDetailsStatusEnum.DELIVERED,
                OrderDetailsStatusEnum.SHIPPED,
                OrderDetailsStatusEnum.PREPARING
        );

        if (requestDTO.getStatus() == null || requestDTO.getStatus().isEmpty()) {
            requestDTO.setStatus(defaultStatusList);
        }

        if(requestDTO.getStartDate() == null ){
            requestDTO.setStartDate(LocalDate.of(1970, 1, 1));
        }

        if(requestDTO.getEndDate() == null ){
            requestDTO.setEndDate(LocalDate.of(9999, 12, 31));
        }
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(requestDTO.getOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(requestDTO.getPageNumber(), requestDTO.getPageSize(), Sort.by(sortDirection, requestDTO.getSortBy()));

        Page<OrderDetail> orderDetails = orderDetailRepository.findAllBySellerAndStatusInAndCreatedAtBetweenAndSearch(
                account,
                requestDTO.getStatus(),
                requestDTO.getStartDate().atStartOfDay(),
                requestDTO.getEndDate().plusDays(1).atStartOfDay(),
                "%" + requestDTO.getSearch() + "%",
                pageable);

        return  orderDetails.map(
                orderDetail -> {
                    OrderDetailResponseDTO responseDTO = OrderDetailResponseDTO.builder()
                            .id(orderDetail.getId())
                            .price(orderDetail.getPrice())
                            .quantity(orderDetail.getQuantity())
                            .flowerListing(FlowerListingMapper.toFlowerListingResponseDTO(orderDetail.getFlowerListing()))
                            .orderSummary(buildOrderResponseDTO(orderDetail.getOrderSummary()))
                            .createAt(orderDetail.getCreatedAt())
                            .status(orderDetail.getStatus())
                            .build();
                    FlowerListingResponseDTO flowerListingResponseDTO = responseDTO.getFlowerListing();
                    flowerListingResponseDTO.setImages(Collections.singletonList(flowerListingService.getFeaturedFlowerImage(orderDetail.getId())));
                    return responseDTO;
                });
    }

    public OrderResponseDTO buildOrderResponseDTO(OrderSummary orderSummary) {
        return OrderResponseDTO.builder()
                .id(orderSummary.getId())
                .buyerName(orderSummary.getBuyerName())
                .buyerEmail(orderSummary.getBuyerEmail())
                .buyerPhone(orderSummary.getBuyerPhone())
                .buyerAddress(orderSummary.getBuyerAddress())
                .note(orderSummary.getNote())
                .build();
    }

    public OrderDetailResponseDTO updateOrder(UpdateOrderDetailRequestDTO requestDTO, Integer orderDetailId){
        Account account = AccountUtils.getCurrentAccount();
        if (account == null || account.getBalance() == null) {
            throw new OrderAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        Optional<OrderDetail> orderDetail = orderDetailRepository.findById(orderDetailId);
        if (!orderDetail.isPresent()) {
            throw new OrderAppException(ErrorCode.ORDER_NOT_FOUND);
        }
        switch (requestDTO.getStatus()) {
            case PREPARING -> {
                if(account.getId().equals(orderDetail.get().getSeller().getId()) && orderDetail.get().getStatus().equals(OrderDetailsStatusEnum.PENDING)){
                    orderDetail.get().setStatus(OrderDetailsStatusEnum.PREPARING);
                    // send notification for user
                }
            }
            case SHIPPED -> {
                if(account.getId().equals(orderDetail.get().getSeller().getId()) && orderDetail.get().getStatus().equals(OrderDetailsStatusEnum.PREPARING)){
                    orderDetail.get().setStatus(OrderDetailsStatusEnum.SHIPPED);
                }
            }
//            case REFUNDED -> {
//                if(account.equals(orderDetail.get().getOrderSummary().getUser())){
//                    orderDetail.get().setStatus(OrderDetailsStatusEnum.REFUNDED);
//                    // another function
//                }
//            }
            case DELIVERED -> {
                if(account.getId().equals(orderDetail.get().getOrderSummary().getUser().getId())){
                    orderDetail.get().setStatus(OrderDetailsStatusEnum.DELIVERED);
                    //send notification for seller & buyer
                }
            }
            case BUYER_CANCELED -> {
                if (orderDetail.get().getStatus() != OrderDetailsStatusEnum.PENDING) {
                    throw new OrderAppException(ErrorCode.ORDER_NOT_CANCELED_BY_BUYER);
                }
                if(account.getId().equals(orderDetail.get().getOrderSummary().getUser().getId())){
                    orderDetail.get().setStatus(OrderDetailsStatusEnum.BUYER_CANCELED);
                    orderDetail.get().setCancelReason(requestDTO.getReason());
                    handleBalanceRefund(orderDetail);
                }
            }
            case SELLER_CANCELED -> {
                if (orderDetail.get().getStatus() != OrderDetailsStatusEnum.PENDING) {
                    throw new OrderAppException(ErrorCode.ORDER_NOT_CANCELED_BY_SELLER);
                }

                if(account.getId().equals(orderDetail.get().getSeller().getId())){
                    orderDetail.get().setStatus(OrderDetailsStatusEnum.SELLER_CANCELED);
                    orderDetail.get().setCancelReason(requestDTO.getReason());

                    handleBalanceRefund(orderDetail);
                }
            }
        }

        OrderDetail orderResponse = orderDetailRepository.save(orderDetail.get());
        return OrderDetailResponseDTO.builder()
                .id(orderResponse.getId())
                .price(orderResponse.getPrice())
                .quantity(orderResponse.getQuantity())
                .flowerListing(FlowerListingMapper.toFlowerListingResponseDTO(orderResponse.getFlowerListing()))
                .orderSummary(buildOrderResponseDTO(orderResponse.getOrderSummary()))
                .createAt(orderResponse.getCreatedAt())
                .status(orderResponse.getStatus())
                .build();
    }

    private void handleBalanceRefund(Optional<OrderDetail> orderDetail) {
        Account accountAdmin = adminService.getAccountAdmin();
        BigDecimal refundBuyer = orderDetail.get().getPrice();
        BigDecimal refundSeller = refundBuyer.multiply(BigDecimal.valueOf(1).subtract(accountAdmin.getBalance()));
        BigDecimal refundAdmin = refundBuyer.subtract(refundSeller);
        // refund buyer
        accountService.handleBalanceByOrder(orderDetail.get().getOrderSummary().getUser(), refundBuyer, WalletLogTypeEnum.ADD, WalletLogActorEnum.BUYER, orderDetail.get().getOrderSummary(), null, WalletLogStatusEnum.SUCCESS, true);
        // seller refund function
        accountService.handleBalanceByOrder(orderDetail.get().getSeller(), refundSeller, WalletLogTypeEnum.SUBTRACT, WalletLogActorEnum.SELLER, orderDetail.get().getOrderSummary(), null, WalletLogStatusEnum.SUCCESS, true);
        // admin refund function
        accountService.handleBalanceByOrder(accountAdmin, refundAdmin, WalletLogTypeEnum.SUBTRACT,WalletLogActorEnum.ADMIN, orderDetail.get().getOrderSummary(), null, WalletLogStatusEnum.SUCCESS, true);

        orderDetail.get().setRefund(true);
    }

    @Override
    public OrderDetailResponseDTO getOrderDetailById(Integer orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId).get();
        return OrderDetailResponseDTO.builder()
                .id(orderDetail.getId())
                .price(orderDetail.getPrice())
                .quantity(orderDetail.getQuantity())
                .flowerListing(FlowerListingMapper.toFlowerListingResponseDTO(orderDetail.getFlowerListing()))
                .orderSummary(buildOrderResponseDTO(orderDetail.getOrderSummary()))
                .createAt(orderDetail.getCreatedAt())
                .status(orderDetail.getStatus())
                .build();
    }

    @Override
    public ReportResponseDTO getReportByAccount(GetReportRequestDTO requestDTO) {
        Account account = AccountUtils.getCurrentAccount();
        if (account == null) {
            throw new OrderAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        if(requestDTO.getStartDate() == null ){
            requestDTO.setStartDate(LocalDate.of(1970, 1, 1));
        }

        if(requestDTO.getEndDate() == null ){
            requestDTO.setEndDate(LocalDate.of(9999, 12, 31));
        }

        List<Object[]> result = orderDetailRepository.countAndSumPriceBySellerAndCreatedAtBetween(
                account.getId(),
                requestDTO.getStartDate().atStartOfDay(),
                requestDTO.getEndDate().plusDays(1).atStartOfDay()
        );
        List<FlowerListingResponseDTO> flowerListingResponseDTOList = flowerListingService.getFlowerListingsByUserID(account.getId());
        int totalCartCount = 0;
        int totalViews = 0;
        for (FlowerListingResponseDTO flower : flowerListingResponseDTOList) {
            int count = cartItemService.countCart(requestDTO, flower.getId());
            totalCartCount += count;
            int viewsCount = auditRepository.countViewByTime(flower.getId(), requestDTO.getStartDate().atStartOfDay(), requestDTO.getEndDate().plusDays(1).atStartOfDay());
            totalViews += viewsCount;
        }
        int totalOrders = ((Number) result.get(0)[1]).intValue();
        double conversionCalculator = ( (double) totalOrders * 100) / totalViews;
        return ReportResponseDTO.builder()
                .totalPrice((BigDecimal) result.get(0)[0])
                .orders(totalOrders)
                .addToCart(totalCartCount)
                .views(totalViews)
                .conversionRate(conversionCalculator)
                .build();

    }

    @Override
    public List<Map<String, Object>> getPriceOverTimeBySellerAndDateRange(LocalDate startDate, LocalDate endDate) {
        Account account = AccountUtils.getCurrentAccount();

        // Retrieve order details within the specified date range
        List<OrderDetail> orderDetails = orderDetailRepository.findOrderDetailsBySellerAndDateRange(
                account.getId(), startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

        // Initialize hourly maps for prices and counts
        Map<Integer, BigDecimal> hourlyPriceMap = IntStream.range(0, 24)
                .boxed()
                .collect(Collectors.toMap(hour -> hour, hour -> BigDecimal.ZERO));

        Map<Integer, Integer> hourlyOrderCountMap = IntStream.range(0, 24)
                .boxed()
                .collect(Collectors.toMap(hour -> hour, hour -> 0));

        // Aggregate actual prices and counts from order details
        orderDetails.forEach(od -> {
            int hour = od.getCreatedAt().getHour(); // Get the hour of the order
            hourlyPriceMap.merge(hour, od.getPrice(), BigDecimal::add);
            hourlyOrderCountMap.merge(hour, 1, Integer::sum);
        });

        // Prepare the result with the hourly data
        return IntStream.range(0, 24)
                .mapToObj(hour -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("time", String.format("%02d:00", hour)); // Format time as "HH:00"
                    map.put("price", hourlyPriceMap.get(hour));
                    map.put("orderCount", hourlyOrderCountMap.get(hour));
                    return map;
                })
                .collect(Collectors.toList());
    }
    @Override
    public CountOrderStatusResponseDTO getCountOrderStatus() {
        Account account = AccountUtils.getCurrentAccount();
        if (account == null) {
            throw new OrderAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        List<Object[]> result = orderDetailRepository.countAllStatusesAndOrderSummary_User_Id(account.getId());
        if (!result.isEmpty()) {
            Object[] counts = result.get(0);
            return new CountOrderStatusResponseDTO(
                    ((Number) counts[0]).intValue(),
                    ((Number) counts[1]).intValue(),
                    ((Number) counts[2]).intValue(),
                    ((Number) counts[3]).intValue(),
                    ((Number) counts[4]).intValue(),
                    ((Number) counts[5]).intValue(),
                    ((Number) counts[6]).intValue()
            );
        }
        return new CountOrderStatusResponseDTO(0, 0, 0, 0, 0, 0, 0);
    }
}
