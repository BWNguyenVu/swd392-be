package com.swd.notification_service.dto.orders;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swd.notification_service.dto.orders.Enum.OrderStatusEnum;
import com.swd.notification_service.dto.orders.Enum.PaymentMethodEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {
    private Integer id;
    private String buyerName;
    private String buyerPhone;
    private String buyerEmail;
    private BigDecimal totalPrice;
    private OrderStatusEnum status;
    private LocalDateTime createAt;
    private Integer accountId;
    private String accountName;
    private String accountEmail;
    private String courses;
    private Boolean isDeleted;
    private PaymentMethodEnum paymentMethod;
}
