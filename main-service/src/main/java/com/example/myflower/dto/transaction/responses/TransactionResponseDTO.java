package com.example.myflower.dto.transaction.responses;

import com.example.myflower.dto.order.responses.OrderResponseDTO;
import com.example.myflower.dto.walletLog.responses.WalletLogResponseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Data
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponseDTO {
    private Integer id;
    private OrderResponseDTO order;
    private WalletLogResponseDTO walletLog;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

}
