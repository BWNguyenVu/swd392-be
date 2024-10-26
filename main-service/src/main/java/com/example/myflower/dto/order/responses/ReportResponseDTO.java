package com.example.myflower.dto.order.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportResponseDTO {
    private Integer addToCart;
    private BigDecimal totalPrice;
    private Integer orders;
    private double conversionRate;
    private Integer views;
}
