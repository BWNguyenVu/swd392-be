package com.example.myflower.dto.order.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportResonseDTO {
    private Integer addToCart;
    private Integer totalPrice;
    private Integer conversionRate;
    private Integer orders;

}
