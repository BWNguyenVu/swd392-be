package com.example.myflower.service.impl;

import com.example.myflower.dto.walletLog.requests.GetWalletLogsRequestDTO;
import com.example.myflower.dto.walletLog.responses.WalletLogResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.Payment;
import com.example.myflower.entity.WalletLog;
import com.example.myflower.entity.enumType.WalletLogStatusEnum;
import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.walletLog.WalletLogAppException;
import com.example.myflower.mapper.WalletLogMapper;
import com.example.myflower.repository.WalletLogRepository;
import com.example.myflower.service.WalletLogService;
import com.example.myflower.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletLogServiceImpl implements WalletLogService {
    @Autowired
    private WalletLogRepository walletLogRepository;
    @Autowired
    private WalletLogMapper walletLogMapper;

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
    public Page<WalletLogResponseDTO> getAllWalletLogByAccount(GetWalletLogsRequestDTO request) {
        Account account = AccountUtils.getCurrentAccount();

        if (account == null || account.getRole() == null) {
            throw new WalletLogAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        List<WalletLogStatusEnum> defaultStatusList = List.of(WalletLogStatusEnum.SUCCESS, WalletLogStatusEnum.FAILED,
                WalletLogStatusEnum.PENDING, WalletLogStatusEnum.EXPIRED);
        List<WalletLogTypeEnum> defaultTypeList = List.of(WalletLogTypeEnum.SUBTRACT, WalletLogTypeEnum.DEPOSIT, WalletLogTypeEnum.WITHDRAW, WalletLogTypeEnum.ADD);
        if (request.getStatus() == null || request.getStatus().isEmpty()) {
            request.setStatus(defaultStatusList);
        }
        if (request.getType() == null || request.getType().isEmpty()) {
            request.setType(defaultTypeList);
        }
        if(request.getStartDate() == null ){
            request.setStartDate(LocalDate.of(1970, 1, 1));
        }

        if(request.getEndDate() == null ){
            request.setEndDate(LocalDate.of(9999, 12, 31));
        }

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(request.getOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.by(sortDirection, request.getSortBy()));

        Page<WalletLog> walletLogs = switch (account.getRole()) {
            case ADMIN -> walletLogRepository.findAll(pageable);
            case USER -> walletLogRepository.findWalletLogByUserAndStatusInAndTypeInAndIsDeletedAndCreatedAtBetween(account, request.getStatus(),request.getType(), false,
                    request.getStartDate() != null ? request.getStartDate().atStartOfDay() : null,
                    request.getEndDate() != null ? request.getEndDate().plusDays(1).atStartOfDay() : null,
                    pageable);
            default -> throw new WalletLogAppException(ErrorCode.WALLET_NOT_FOUND);
        };

        return walletLogs.map(walletLog -> walletLogMapper.buildWalletLogResponseDTO(walletLog, walletLog.getPayment()));
    }


    @Override
    public WalletLogResponseDTO getWalletLogById(Integer walletLogId) {
        Account account = AccountUtils.getCurrentAccount();
        WalletLog walletLog = walletLogRepository.findByIdAndUser(walletLogId, account)
                .orElseThrow(() -> new WalletLogAppException(ErrorCode.WALLET_NOT_FOUND));

        return walletLogMapper.buildWalletLogResponseDTO(walletLog, walletLog.getPayment());
    }

    @Override
    @Transactional
    public void softDeleteWalletLog(Integer walletLogId) {
        WalletLog walletLog = walletLogRepository.findById(walletLogId)
                .orElseThrow(() -> new WalletLogAppException(ErrorCode.WALLET_NOT_FOUND));

        walletLog.setDeleted(true);
        walletLog.setUpdatedAt(LocalDateTime.now());
        walletLogRepository.save(walletLog);
    }

}
