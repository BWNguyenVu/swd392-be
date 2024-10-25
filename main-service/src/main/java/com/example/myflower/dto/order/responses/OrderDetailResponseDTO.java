package com.example.myflower.dto.order.responses;

import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.dto.payment.responses.PaymentResponseDTO;
import com.example.myflower.entity.OrderSummary;
import com.example.myflower.entity.enumType.OrderDetailsStatusEnum;
import com.example.myflower.entity.enumType.PaymentMethodEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
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
