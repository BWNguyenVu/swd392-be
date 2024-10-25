package com.example.myflower.dto.order.requests;

import com.example.myflower.entity.enumType.PaymentMethodEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
public class OrderDetailRequestDTO {
    private Integer flowerListingId;
    private Integer quantity;
    private BigDecimal price;
}
