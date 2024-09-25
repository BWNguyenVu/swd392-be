package com.example.myflower.service;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.WalletLog;

public interface IWalletLogService {
    WalletLog createWalletLog(WalletLog walletLog, Account account);
}
