package com.swd.notification_service.dto.wallets;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataResponse {
    private Integer id;
    private BigDecimal remainingBalance;
    private BigDecimal cashoutBalance;
    private BigDecimal initialBalance;
    private String bankName;
    private String bankAccountNumber;
    private WalletLog walletLog;
    private String name;
    private String email;
    private String tokens;

    public DataResponse(Integer id, BigDecimal remainingBalance, BigDecimal cashoutBalance, BigDecimal initialBalance, String bankName, String bankAccountNumber, WalletLog walletLog, String name, String email, String tokens) {
        this.id = id;
        this.remainingBalance = remainingBalance;
        this.cashoutBalance = cashoutBalance;
        this.initialBalance = initialBalance;
        this.bankName = bankName;
        this.bankAccountNumber = bankAccountNumber;
        this.walletLog = walletLog;
        this.name = name;
        this.email = email;
        this.tokens = tokens;
    }
}
