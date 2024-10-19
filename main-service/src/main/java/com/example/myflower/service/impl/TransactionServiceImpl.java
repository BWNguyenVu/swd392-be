package com.example.myflower.service.impl;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.order.responses.OrderResponseDTO;
import com.example.myflower.dto.transaction.responses.TransactionResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.OrderSummary;
import com.example.myflower.entity.Transaction;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.transaction.TransactionAppException;
import com.example.myflower.exception.walletLog.WalletLogAppException;
import com.example.myflower.mapper.WalletLogMapper;
import com.example.myflower.repository.TransactionRepository;
import com.example.myflower.service.OrderService;
import com.example.myflower.service.TransactionService;
import com.example.myflower.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private WalletLogMapper walletLogMapper;

    @Autowired
    @Lazy
    private OrderService orderService;

    @Override
    public BaseResponseDTO getTransactionByAccount() {
        final String message = "Get transaction by account successfully";
        Account user = AccountUtils.getCurrentAccount();
        if (user == null || user.getBalance() == null) {
            throw new WalletLogAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        List<Transaction> transactions = switch (user.getRole()) {
            case ADMIN -> transactionRepository.findAll();
            case USER -> transactionRepository.findTransactionByUser(user);
            default -> throw new TransactionAppException(ErrorCode.TRANSACTION_NOT_FOUND);
        };

        List<TransactionResponseDTO> transactionResponseDTOs = transactions.stream().map(
                transaction -> TransactionResponseDTO.builder()
                        .id(transaction.getId())
                        .order(buildOrderResponseDTO(transaction.getOrderSummary(), user))
                        .walletLog(walletLogMapper.buildWalletLogResponseDTO(transaction.getWalletLog(), null))
                        .createAt(transaction.getCreatedAt())
                        .updateAt(transaction.getUpdatedAt())
                        .build()).toList();

        return BaseResponseDTO.builder()
                .message(message)
                .success(true)
                .data(transactionResponseDTOs)
                .build();
    }

    private OrderResponseDTO buildOrderResponseDTO(OrderSummary order, Account user) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .totalAmount(order.getTotalPrice())
                .note(order.getNote())
                .balance(user.getBalance())
                .orderDetails(orderService.getAllOrderDetailsByOrderSummaryId(order.getId()))
                .createdAt(order.getCreatedAt())
                .build();
    }

    public Transaction createTransaction(Transaction transaction, Account account) {
            Transaction.builder()
                .user(account)
                .orderSummary(transaction.getOrderSummary())
                .walletLog(transaction.getWalletLog() != null ? transaction.getWalletLog() : null)
                .createdAt(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);
        return transaction;
    }
    @Override
    public BaseResponseDTO getTransactionById(Integer id) {
        final String message = "Get transaction by ID successfully";
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionAppException(ErrorCode.TRANSACTION_NOT_FOUND));

        TransactionResponseDTO transactionResponseDTO = TransactionResponseDTO.builder()
                .id(transaction.getId())
                .order(buildOrderResponseDTO(transaction.getOrderSummary(), transaction.getUser()))
                .walletLog(walletLogMapper.buildWalletLogResponseDTO(transaction.getWalletLog(), null))
                .createAt(transaction.getCreatedAt())
                .updateAt(transaction.getUpdatedAt())
                .build();

        return BaseResponseDTO.builder()
                .message(message)
                .success(true)
                .data(transactionResponseDTO)
                .build();
    }

    @Override
    public void softDeleteTransaction(Integer transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionAppException(ErrorCode.TRANSACTION_NOT_FOUND));

        transaction.setDeleted(true);
        transactionRepository.save(transaction);
    }

}
