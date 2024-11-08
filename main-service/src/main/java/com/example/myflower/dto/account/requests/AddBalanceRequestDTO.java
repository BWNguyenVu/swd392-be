package com.example.myflower.dto.account.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AddBalanceRequestDTO {
    @Min(value = 20000, message = "Add balance amount must be greater than 20,000 VND")
    @Max(value = 10000000, message = "Add balance amount must not exceed 10,000,000 VND")
    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;
}