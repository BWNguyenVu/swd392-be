package com.example.myflower.dto.order.requests;

import com.example.myflower.entity.enumType.OrderDetailsStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class UpdateOrderDetailRequestDTO {
    private String reason;
    @NotNull(message = "Order status is required")
    private OrderDetailsStatusEnum status;
}
