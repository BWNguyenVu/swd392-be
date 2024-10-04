package com.example.myflower.dto.order.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailRequestDTO {
    private Integer flowerListingId;
    private Integer quantity;
    private BigDecimal price;
}
