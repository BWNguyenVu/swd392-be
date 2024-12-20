package com.example.myflower.dto.order.responses;

import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.entity.enumType.OrderDetailsStatusEnum;
import com.example.myflower.entity.enumType.PaymentMethodEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailResponseDTO {
    private Integer id;
    private FlowerListingResponseDTO flowerListing;
    private OrderResponseDTO orderSummary;
    private Integer quantity;
    private BigDecimal price;
    private OrderDetailsStatusEnum status;
    private PaymentMethodEnum paymentMethod;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
