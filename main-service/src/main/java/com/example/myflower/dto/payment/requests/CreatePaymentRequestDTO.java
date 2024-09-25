package com.example.myflower.dto.payment.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.payos.type.ItemData;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePaymentRequestDTO {
    private BigDecimal totalAmount;
    private String note;
    private ItemData item;
}
