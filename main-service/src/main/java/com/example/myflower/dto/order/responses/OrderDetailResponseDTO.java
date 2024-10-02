package com.example.myflower.dto.order.responses;

import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailResponseDTO {
    private FlowerListingResponseDTO flowerListing;
    private Integer quantity;
    private BigDecimal price;
}
