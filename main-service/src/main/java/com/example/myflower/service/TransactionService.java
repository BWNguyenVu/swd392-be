package com.example.myflower.service;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.Transaction;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction, Account account);
}
