package com.example.myflower.service.impl;

import com.example.myflower.dto.order.requests.CreateOrderRequestDTO;
import com.example.myflower.dto.order.requests.OrderDetailRequestDTO;
import com.example.myflower.dto.order.responses.OrderByWalletResponseDTO;
import com.example.myflower.entity.*;
import com.example.myflower.entity.enumType.OrderStatusEnum;
import com.example.myflower.entity.enumType.WalletLogActorEnum;
import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.order.OrderAppException;
import com.example.myflower.repository.*;
import com.example.myflower.service.AccountService;
import com.example.myflower.service.OrderService;
import com.example.myflower.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private AdminServiceImpl adminServiceImpl;

    @Autowired
    private AccountService accountService;

    @Override
    public OrderByWalletResponseDTO orderByWallet(CreateOrderRequestDTO orderDTO) throws OrderAppException {
        // Get the current user account
        Account account = AccountUtils.getCurrentAccount();

        if (account == null) {
            throw new OrderAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        BigDecimal totalPrice = orderDTO.getOrderDetails().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (account.getBalance() == null || account.getBalance().compareTo(totalPrice) < 0) {
            throw new OrderAppException(ErrorCode.ORDER_INVALID_FUNDS);
        }

        // Create order summary
        OrderSummary orderSummary = createOrderFromDTO(orderDTO, account, totalPrice);
        orderSummaryRepository.save(orderSummary);

        distributeBalance(account, totalPrice, orderSummary);

        // Create order details
        List<OrderDetail> orderDetails = createOrderDetails(orderDTO, orderSummary, account);

        // Return response with order details
        return createOrderByWalletResponseDTO(orderSummary, account, orderDetails);
    }

    private void distributeBalance(Account accountBuyer, BigDecimal totalPrice, OrderSummary orderSummary) {
        Account accountAdmin = adminServiceImpl.getAccountAdmin();
        // subtract balance for buyer
        accountService.handleBalanceByOrder(accountBuyer, totalPrice, WalletLogTypeEnum.SUBTRACT, WalletLogActorEnum.BUYER, orderSummary);
        for (Map.Entry<Account, BigDecimal> entry : sellerBalanceMap.entrySet()) {
            Account accountSeller = entry.getKey();
            BigDecimal amountInitial = entry.getValue();
            // handle calculator
            BigDecimal calculatorAmountForSeller = amountInitial.divide(accountAdmin.getFeeService(), 2, RoundingMode.HALF_UP);
            // add balance for seller
            accountService.handleBalanceByOrder(accountSeller, calculatorAmountForSeller, WalletLogTypeEnum.ADD, WalletLogActorEnum.SELLER,orderSummary);
        }

        // add balance for admin
        accountService.handleBalanceByOrder(accountAdmin, totalPrice.multiply(accountAdmin.getFeeService()), WalletLogTypeEnum.ADD, WalletLogActorEnum.ADMIN ,orderSummary);
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

    private List<OrderDetail> createOrderDetails(CreateOrderRequestDTO orderDTO, OrderSummary orderSummary, Account account) throws OrderAppException {
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailRequestDTO item : orderDTO.getOrderDetails()) {

            FlowerListing flowerListing = flowerListingRepository.findById(item.getFlowerListingId())
                    .orElseThrow(() -> new OrderAppException(ErrorCode.FLOWER_NOT_FOUND));

            Account seller = flowerListing.getUser();
            sellerBalanceMap.merge(seller, item.getPrice(), BigDecimal::add);

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

    private OrderByWalletResponseDTO createOrderByWalletResponseDTO(OrderSummary orderSummary, Account account, List<OrderDetail> orderDetails) {
        return OrderByWalletResponseDTO.builder()
                .message("Order by wallet successfully!")
                .error(false)
                .id(orderSummary.getId())
                .totalAmount(orderSummary.getTotalPrice())
                .balance(account.getBalance())
                .status(OrderStatusEnum.SUCCESS)
                .note(orderSummary.getNote())
                .orderDetails(orderDetails)
                .build();
    }
}
