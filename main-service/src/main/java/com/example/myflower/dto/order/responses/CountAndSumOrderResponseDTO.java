package com.example.myflower.dto.order.responses;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CountAndSumOrderResponseDTO {
    private BigDecimal totalPrice;
    private Integer orders;
}
