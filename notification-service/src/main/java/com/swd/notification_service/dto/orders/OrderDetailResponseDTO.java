package com.swd.notification_service.dto.orders;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swd.notification_service.dto.flowers.FlowerListingResponseDTO;
import com.swd.notification_service.dto.orders.Enum.OrderDetailsStatusEnum;
import com.swd.notification_service.dto.orders.Enum.PaymentMethodEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
