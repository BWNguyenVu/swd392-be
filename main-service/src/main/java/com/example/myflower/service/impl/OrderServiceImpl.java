package com.example.myflower.service.impl;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.dto.order.requests.CreateOrderRequestDTO;
import com.example.myflower.dto.order.requests.OrderDetailRequestDTO;
import com.example.myflower.dto.order.responses.OrderResponseDTO;
import com.example.myflower.dto.order.responses.OrderDetailResponseDTO;
import com.example.myflower.entity.*;
import com.example.myflower.entity.enumType.OrderStatusEnum;
import com.example.myflower.entity.enumType.WalletLogActorEnum;
import com.example.myflower.entity.enumType.WalletLogStatusEnum;
import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.order.OrderAppException;
import com.example.myflower.repository.*;
import com.example.myflower.service.AccountService;
import com.example.myflower.service.AdminService;
import com.example.myflower.service.OrderService;
import com.example.myflower.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderSummaryRepository orderSummaryRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private FlowerListingRepository flowerListingRepository;

    private final Map<Account, BigDecimal> sellerBalanceMap = new HashMap<>();

    @Autowired
    private AdminService adminService;

    @Autowired
    private AccountService accountService;

    @Override
    public OrderResponseDTO orderByWallet(CreateOrderRequestDTO orderDTO) throws OrderAppException {
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
        return createOrderByWalletResponseDTO(orderSummary, account, orderDetailsResponseDTO);
    }

    private void distributeBalance(Account accountBuyer, BigDecimal totalPrice, OrderSummary orderSummary, Map<Account, BigDecimal> sellerBalanceMap) {
        Account accountAdmin = adminService.getAccountAdmin();
        // subtract balance for buyer
        accountService.handleBalanceByOrder(accountBuyer, totalPrice, WalletLogTypeEnum.SUBTRACT, WalletLogActorEnum.BUYER, orderSummary, null, WalletLogStatusEnum.SUCCESS);
        for (Map.Entry<Account, BigDecimal> entry : sellerBalanceMap.entrySet()) {
            Account accountSeller = entry.getKey();
            BigDecimal amountInitial = entry.getValue();
            // handle calculator
            BigDecimal calculatorAmountForSeller = amountInitial.multiply(BigDecimal.valueOf(1).subtract(accountAdmin.getFeeService()));
            // add balance for seller
            accountService.handleBalanceByOrder(accountSeller, calculatorAmountForSeller, WalletLogTypeEnum.ADD, WalletLogActorEnum.SELLER, orderSummary, null, WalletLogStatusEnum.SUCCESS);
        }

        // add balance for admin
        accountService.handleBalanceByOrder(accountAdmin, totalPrice.multiply(accountAdmin.getFeeService()), WalletLogTypeEnum.ADD, WalletLogActorEnum.ADMIN ,orderSummary, null, WalletLogStatusEnum.SUCCESS);
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
                .status(OrderStatusEnum.PENDING)
                .note(orderDTO.getNote())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<OrderDetail> createOrderDetails(CreateOrderRequestDTO orderDTO, OrderSummary orderSummary, Account account, Map<Account, BigDecimal> sellerBalanceMap) throws OrderAppException {
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailRequestDTO item : orderDTO.getOrderDetails()) {

            FlowerListing flowerListing = flowerListingRepository.findById(item.getFlowerListingId())
                    .orElseThrow(() -> new OrderAppException(ErrorCode.FLOWER_NOT_FOUND));

            Account seller = flowerListing.getUser();
            sellerBalanceMap.merge(seller, item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())), BigDecimal::add);

            OrderDetail orderDetail = OrderDetail.builder()
                    .orderSummary(orderSummary)
                    .user(account)
                    .flowerListing(flowerListing)
                    .price(item.getPrice())
                    .quantity(item.getQuantity())
                    .createdAt(LocalDateTime.now())
                    .build();

            orderDetailRepository.save(orderDetail);
            orderDetails.add(orderDetail);
        }
        return orderDetails;
    }

    private OrderResponseDTO createOrderByWalletResponseDTO(OrderSummary orderSummary, Account account, List<OrderDetailResponseDTO> orderDetailsResponseDTO) {
        return OrderResponseDTO.builder()
                .message("Order by wallet successfully!")
                .error(false)
                .id(orderSummary.getId())
                .totalAmount(orderSummary.getTotalPrice())
                .balance(account.getBalance())
                .status(OrderStatusEnum.SUCCESS)
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

    private OrderDetailResponseDTO convertOrderDetailDTO(OrderDetail orderDetail) {
        FlowerListingResponseDTO flowerListingResponseDTO = FlowerListingResponseDTO.builder()
                .id(orderDetail.getId())
                .name(orderDetail.getFlowerListing().getName())
                .imageUrl(orderDetail.getFlowerListing().getImageUrl())
                .description(orderDetail.getFlowerListing().getDescription())
                .build();
        return OrderDetailResponseDTO.builder()
                .flowerListing(flowerListingResponseDTO)
                .price(orderDetail.getPrice())
                .quantity(orderDetail.getQuantity())
                .build();
    }

    public List<OrderDetailResponseDTO> getAllOrderDetailsByOrderSummaryId(Integer orderSummaryId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findOrderDetailByOrderSummaryId(orderSummaryId);
        return convertOrderDetailDTO(orderDetails);
    }

    public BaseResponseDTO getAllOrderByAccount() {
        Account account = AccountUtils.getCurrentAccount();
        List<OrderSummary> orderSummarys = orderSummaryRepository.findOrderSummariesByUser(account);
        orderSummarys.stream().map(
                orderSummary -> OrderResponseDTO.builder()
                        .id(orderSummary.getId())
                        .balance(account.getBalance())
                        .orderDetails(getAllOrderDetailsByOrderSummaryId(orderSummary.getId()))
                        .note(orderSummary.getNote())
                        .status(orderSummary.getStatus())
                        .createdAt(orderSummary.getCreatedAt())
                        .totalAmount(orderSummary.getTotalPrice())
                        .build()
        ).toList();
        return BaseResponseDTO.builder()
                .data(orderSummarys)
                .message("Get Orders successfully!")
                .success(true)
                .build();
    }
}
