package com.example.myflower.service;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.Payment;
import com.example.myflower.entity.WalletLog;
import com.example.myflower.entity.enumType.WalletLogStatusEnum;

public interface WalletLogService {
    WalletLog createWalletLog(WalletLog walletLog, Account account);
    WalletLog updateWalletLogByPayment(Payment payment, WalletLogStatusEnum status);
}
