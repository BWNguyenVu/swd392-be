package com.example.myflower.dto.account.responses;

import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class AddBalanceResponseDTO {
    private Integer id;
    private Integer orderCode;
    private BigDecimal addBalance;
    private BigDecimal currentBalance;
    private String currency;
    private String note;
    private WalletLogTypeEnum walletLogTypeEnum;
    private String checkoutUrl;
    private String paymentLinkId;
    private LocalDateTime createAt;
    private String message;
    private Integer error;

}
