package com.example.myflower.dto.account.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBalanceRequestDTO {
    @Min(value = 20000, message = "Add balance amount must be greater than 20.000 VND")
    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;
}