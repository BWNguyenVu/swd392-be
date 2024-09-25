package com.example.myflower.dto.account.responses;

import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddBalanceResponseDTO {
    private Integer orderCode;
    private BigDecimal addBalance;
    private BigDecimal currentBalance;
    private String currency;
    private String note;
    private WalletLogTypeEnum walletLogTypeEnum;
    private String checkoutUrl;
    private String message;
    private Integer error;

    public AddBalanceResponseDTO(Integer orderCode, BigDecimal addBalance, BigDecimal currentBalance, String currency, String note, WalletLogTypeEnum walletLogTypeEnum, String checkoutUrl, String message, Integer error) {
        this.orderCode = orderCode;
        this.addBalance = addBalance;
        this.currentBalance = currentBalance;
        this.currency = currency;
        this.note = note;
        this.walletLogTypeEnum = walletLogTypeEnum;
        this.checkoutUrl = checkoutUrl;
        this.message = message;
        this.error = error;
    }
}
