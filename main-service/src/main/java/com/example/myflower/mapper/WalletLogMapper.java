package com.example.myflower.mapper;

import com.example.myflower.dto.walletLog.responses.WalletLogResponseDTO;
import com.example.myflower.entity.WalletLog;

public class WalletLogMapper {
    public static WalletLogResponseDTO buildWalletLogResponseDTO(WalletLog walletLog) {
        return WalletLogResponseDTO.builder()
                .id(walletLog.getId())
                .account(AccountMapper.mapToAccountResponseDTO(walletLog.getUser()))
                .type(walletLog.getType())
                .actor(walletLog.getActorEnum())
                .amount(walletLog.getAmount())
                .paymentMethod(walletLog.getPaymentMethod())
                .status(walletLog.getStatus())
                .createdAt(walletLog.getCreatedAt())
                .updatedAt(walletLog.getUpdatedAt())
                .build();
    }
}
