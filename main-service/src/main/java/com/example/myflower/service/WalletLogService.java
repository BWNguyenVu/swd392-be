package com.example.myflower.service;

import com.example.myflower.dto.walletLog.responses.WalletLogResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.Payment;
import com.example.myflower.entity.WalletLog;
import com.example.myflower.entity.enumType.WalletLogStatusEnum;

import java.util.List;

public interface WalletLogService {
    WalletLog createWalletLog(WalletLog walletLog, Account account);
    WalletLog updateWalletLogByPayment(Payment payment, WalletLogStatusEnum status);
    List<WalletLogResponseDTO> getAllWalletLogByAccount();
    WalletLogResponseDTO getWalletLogById(Integer id);
    void softDeleteWalletLog(Integer id);
}
