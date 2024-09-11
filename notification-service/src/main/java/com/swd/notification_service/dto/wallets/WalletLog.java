package com.swd.notification_service.dto.wallets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swd.notification_service.dto.wallets.Enum.WalletLogTypeEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WalletLog {
    private Integer id;

    private WalletLogTypeEnum type;

    private BigDecimal amount;

    private LocalDateTime createAt;

    private Boolean isDeleted;
}
