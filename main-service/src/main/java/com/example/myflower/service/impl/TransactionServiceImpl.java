package com.example.myflower.service.impl;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.Transaction;
import com.example.myflower.entity.WalletLog;
import com.example.myflower.repository.TransactionRepository;
import com.example.myflower.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

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
}
