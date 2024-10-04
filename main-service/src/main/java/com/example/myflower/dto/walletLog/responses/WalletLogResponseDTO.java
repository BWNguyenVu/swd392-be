package com.example.myflower.dto.walletLog.responses;

import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.entity.enumType.PaymentMethodEnum;
import com.example.myflower.entity.enumType.WalletLogActorEnum;
import com.example.myflower.entity.enumType.WalletLogStatusEnum;
import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletLogResponseDTO {
    private Integer id;
    private AccountResponseDTO account;
    private WalletLogTypeEnum type;
    private WalletLogActorEnum actor;
    private BigDecimal amount;
    private PaymentMethodEnum paymentMethod;
    private WalletLogStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
