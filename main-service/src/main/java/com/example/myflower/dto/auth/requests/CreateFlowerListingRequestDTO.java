package com.example.myflower.dto.auth.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFlowerListingRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockBalance;
    private String address;
}
