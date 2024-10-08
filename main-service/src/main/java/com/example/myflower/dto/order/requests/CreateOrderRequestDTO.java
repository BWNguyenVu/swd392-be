package com.example.myflower.dto.order.requests;

import com.example.myflower.entity.enumType.PaymentMethodEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequestDTO {
    private String buyerName;
    private String buyerAddress;
    private String buyerPhone;
    private PaymentMethodEnum paymentMethod;
    private String note;
    private List<OrderDetailRequestDTO> orderDetails;
}
