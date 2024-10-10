package com.example.myflower.dto.payment.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Builder
@Data
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponseDTO {
    private Integer id;
    private BigDecimal amount;
    private String currency;
    private String note;
    private Integer orderCode;
    private String checkoutUrl;
    private String paymentLinkId;
    private LocalDateTime createAt;

}
