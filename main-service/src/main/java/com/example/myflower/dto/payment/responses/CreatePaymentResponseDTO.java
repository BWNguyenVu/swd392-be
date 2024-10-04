package com.example.myflower.dto.payment.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePaymentResponseDTO {
    private Integer id;
    private BigDecimal amount;
    private String currency;
    private String note;
    private Integer orderCode;
    private String checkoutUrl;
    private String qrCode;
    private String paymentLinkId;
    private LocalDateTime createAt;

    public CreatePaymentResponseDTO(Integer id, BigDecimal amount, String currency, String note, Integer orderCode, String checkoutUrl, String qrCode, String paymentLinkId, LocalDateTime createAt) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.note = note;
        this.orderCode = orderCode;
        this.checkoutUrl = checkoutUrl;
        this.qrCode = qrCode;
        this.paymentLinkId = paymentLinkId;
        this.createAt = createAt;
    }
}
