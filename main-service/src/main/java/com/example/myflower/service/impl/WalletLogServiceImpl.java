package com.example.myflower.service.impl;

import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.walletLog.responses.WalletLogResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.Payment;
import com.example.myflower.entity.WalletLog;
import com.example.myflower.entity.enumType.WalletLogStatusEnum;
import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import com.example.myflower.mapper.AccountMapper;
import com.example.myflower.repository.WalletLogRepository;
import com.example.myflower.service.WalletLogService;
import com.example.myflower.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletLogServiceImpl implements WalletLogService {
    @Autowired
    private WalletLogRepository walletLogRepository;
    @Override
    @Transactional
    public WalletLog createWalletLog(WalletLog walletLog, Account account) {
        walletLog.setActorEnum(walletLog.getActorEnum());
        walletLog.setUser(account);
        walletLog.setAmount(walletLog.getAmount());
        walletLog.setType(walletLog.getType());
        walletLog.setStatus(walletLog.getStatus());
        walletLog.setCreatedAt(LocalDateTime.now());
        walletLog.setPaymentMethod(walletLog.getPaymentMethod());
        walletLog.setPayment(walletLog.getPayment());
        walletLogRepository.save(walletLog);
        return walletLog;
    }
    @Override
    public WalletLog updateWalletLogByPayment(Payment payment, WalletLogStatusEnum status) {
        WalletLog walletLog = walletLogRepository.findWalletLogByPayment(payment);
        walletLog.setStatus(status);
        walletLog.setUpdatedAt(LocalDateTime.now());
        if (status.equals(WalletLogStatusEnum.SUCCESS)) {
            walletLog.setClosed(true);
        }
        walletLogRepository.save(walletLog);
        return walletLog;
    }
    @Override
    public List<WalletLogResponseDTO> getAllWalletLogByAccount() {
        Account account = AccountUtils.getCurrentAccount();
        List<WalletLog> walletLogs = walletLogRepository.findWalletLogByUser(account);
        AccountResponseDTO accountResponseDTO  = AccountMapper.mapToAccountResponseDTO(account);
        return walletLogs.stream()
                .map(walletLog -> WalletLogResponseDTO.builder()
                        .id(walletLog.getId())
                        .account(accountResponseDTO)
                        .type(walletLog.getType())
                        .actor(walletLog.getActorEnum())
                        .amount(walletLog.getAmount())
                        .paymentMethod(walletLog.getPaymentMethod())
                        .status(walletLog.getStatus())
                        .createdAt(walletLog.getCreatedAt())
                        .updatedAt(walletLog.getUpdatedAt())
                        .build())
                .toList();
    }
}
