package com.example.myflower.dto.order.responses;

import com.example.myflower.entity.enumType.OrderStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
