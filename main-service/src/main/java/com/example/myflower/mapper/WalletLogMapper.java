package com.example.myflower.mapper;

import com.example.myflower.dto.walletLog.responses.WalletLogResponseDTO;
import com.example.myflower.entity.Payment;
import com.example.myflower.entity.WalletLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WalletLogMapper {
    @Autowired
    private AccountMapper accountMapper;

    public WalletLogResponseDTO buildWalletLogResponseDTO(WalletLog walletLog, Payment payment) {
        return WalletLogResponseDTO.builder()
                .id(walletLog.getId())
                .account(accountMapper.mapToAccountResponseDTO(walletLog.getUser()))
                .type(walletLog.getType())
                .actor(walletLog.getActorEnum())
                .amount(walletLog.getAmount())
                .paymentMethod(walletLog.getPaymentMethod())
                .payment(payment == null ? null : PaymentMapper.buildPaymentResponseDTO(payment))
                .status(walletLog.getStatus())
                .createdAt(walletLog.getCreatedAt())
                .updatedAt(walletLog.getUpdatedAt())
                .build();
    }
}
