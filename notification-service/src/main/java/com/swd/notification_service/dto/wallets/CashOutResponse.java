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
public class CashOutResponse {
    private String message;
    private String error;
    private Integer code;
    private DataResponse data;

    public CashOutResponse(String message, String error, Integer code, DataResponse data) {
        this.message = message;
        this.error = error;
        this.code = code;
        this.data = data;
    }

    @Getter
    @Setter
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DataResponse {
        private Integer id;
        private BigDecimal remainingBalance;
        private BigDecimal cashoutBalance;
        private BigDecimal initialBalance;
        private String bankName;
        private String bankAccountNumber;
        private WalletLog walletLog;

        public DataResponse(Integer id, BigDecimal remainingBalance, BigDecimal cashoutBalance, BigDecimal initialBalance, String bankName, String bankAccountNumber , WalletLog walletLog) {
            this.id = id;
            this.remainingBalance = remainingBalance;
            this.cashoutBalance = cashoutBalance;
            this.initialBalance = initialBalance;
            this.bankName = bankName;
            this.bankAccountNumber = bankAccountNumber;
            this.walletLog = walletLog;
        }
    }
}
