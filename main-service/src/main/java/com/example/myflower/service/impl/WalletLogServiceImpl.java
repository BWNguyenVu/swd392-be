package com.example.myflower.service.impl;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.WalletLog;
import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import com.example.myflower.repository.WalletLogRepository;
import com.example.myflower.service.WalletLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class WalletLogServiceImpl implements WalletLogService {
    @Autowired
    private WalletLogRepository walletLogRepository;

    @Transactional
    public WalletLog createWalletLog(WalletLog walletLog, Account account) {
        walletLog.setActorEnum(walletLog.getActorEnum());
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
