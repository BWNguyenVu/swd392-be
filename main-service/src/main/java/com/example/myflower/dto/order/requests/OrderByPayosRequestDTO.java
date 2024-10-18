package com.example.myflower.dto.order.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderByPayosRequestDTO {
    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;
    private List<OrderDetailRequestDTO> orderDetails;
}
