package com.example.myflower.service;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.Transaction;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction, Account account);
    BaseResponseDTO getTransactionByAccount();
    BaseResponseDTO getTransactionById(Integer transactionId);
    void softDeleteTransaction(Integer transactionId);
}
