package com.example.myflower.service.impl;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.WalletLog;
import com.example.myflower.entity.enumType.WalletLogStatusEnum;
import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import com.example.myflower.repository.WalletLogRepository;
import com.example.myflower.service.IWalletLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WalletLogServiceImpl implements IWalletLogService {
    private final WalletLogRepository walletLogRepository;

    public WalletLogServiceImpl(WalletLogRepository walletLogRepository) {
        this.walletLogRepository = walletLogRepository;
    }

    public WalletLog createWalletLog(WalletLog walletLog, Account account) {
        walletLog.setUser(account);
        walletLog.setAmount(walletLog.getAmount());
        walletLog.setType(walletLog.getType());
        walletLog.setStatus(walletLog.getStatus());
        walletLog.setCreatedAt(LocalDateTime.now());
        walletLog.setPaymentMethod(walletLog.getPaymentMethod());
        walletLogRepository.save(walletLog);
        return walletLog;
    }
}
