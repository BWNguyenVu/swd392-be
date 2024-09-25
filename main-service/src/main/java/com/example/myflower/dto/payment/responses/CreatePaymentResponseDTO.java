package com.example.myflower.dto.payment.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePaymentResponseDTO {
    private BigDecimal amount;
    private String currency;
    private String note;
    private Integer orderCode;
    private String checkoutUrl;
    private String qrCode;

    public CreatePaymentResponseDTO(BigDecimal amount, String currency, String note, Integer orderCode, String checkoutUrl, String qrCode) {
        this.amount = amount;
        this.currency = currency;
        this.note = note;
        this.orderCode = orderCode;
        this.checkoutUrl = checkoutUrl;
        this.qrCode = qrCode;
    }
}
