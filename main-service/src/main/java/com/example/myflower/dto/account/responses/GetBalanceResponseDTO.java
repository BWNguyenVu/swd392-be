package com.example.myflower.dto.account.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetBalanceResponseDTO {
    private String name;
    private BigDecimal balance;
}
