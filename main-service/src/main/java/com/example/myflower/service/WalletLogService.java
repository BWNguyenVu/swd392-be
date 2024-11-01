package com.example.myflower.service;

import com.example.myflower.dto.walletLog.requests.GetWalletLogsRequestDTO;
import com.example.myflower.dto.walletLog.responses.WalletLogResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.Payment;
import com.example.myflower.entity.WalletLog;
import com.example.myflower.entity.enumType.WalletLogStatusEnum;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface WalletLogService {
    WalletLog createWalletLog(WalletLog walletLog, Account account);
    WalletLog updateWalletLogByPayment(Payment payment, WalletLogStatusEnum status, BigDecimal balance);
    Page<WalletLogResponseDTO> getAllWalletLogByAccount(GetWalletLogsRequestDTO getWalletLogsRequestDTO);
    WalletLogResponseDTO getWalletLogById(Integer id);
    void softDeleteWalletLog(Integer id);
}
