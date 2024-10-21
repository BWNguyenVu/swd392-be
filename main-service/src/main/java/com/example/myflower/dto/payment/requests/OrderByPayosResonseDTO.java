package com.example.myflower.dto.payment.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.payos.type.ItemData;

import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderByPayosResonseDTO {
    private BigDecimal totalAmount;
    private String note;
    private List<ItemData> item;
}
