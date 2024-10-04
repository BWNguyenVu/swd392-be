package com.example.myflower.dto.account.responses;

import com.example.myflower.dto.walletLog.responses.WalletLogResponseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnAddBalanceResponseDTO {
    private String message;
    private boolean success;
    private Integer code;
    private DataResponse data;

    @Getter
    @Setter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DataResponse {
        private Integer id;
        private BigDecimal balance;
        private BigDecimal amount;
        private WalletLogResponseDTO walletLog;
        private String name;
        private String email;
    }
}
