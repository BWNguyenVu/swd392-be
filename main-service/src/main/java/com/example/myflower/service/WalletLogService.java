package com.example.myflower.service;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.WalletLog;

public interface WalletLogService {
    WalletLog createWalletLog(WalletLog walletLog, Account account);
}
