package com.example.myflower.dto.order.responses;

import com.example.myflower.entity.FlowerListing;
import com.example.myflower.entity.OrderDetail;
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
public class OrderByWalletResponseDTO {
    private String message;
    private Boolean error;
    private Integer id;
    private BigDecimal totalAmount;
    private String note;
    private BigDecimal balance;
    private List<OrderDetailResponseDTO> orderDetails;
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;
    private LocalDateTime createdAt;
}
