package com.swd.notification_service.dto.orders;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
public class OrderResponseDTO {
    private String message;
    private Boolean error;
    private Integer id;
    private BigDecimal totalAmount;
    private String note;
    private BigDecimal balance;
    private String buyerName;
    private String buyerPhone;
    private String buyerEmail;
    private String buyerAddress;
    private List<OrderDetailResponseDTO> orderDetails;
    private LocalDateTime createdAt;
}
