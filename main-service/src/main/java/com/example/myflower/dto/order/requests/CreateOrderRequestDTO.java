package com.example.myflower.dto.order.requests;

import com.example.myflower.entity.enumType.PaymentMethodEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateOrderRequestDTO {
    private String buyerName;
    private String buyerAddress;
    private String buyerPhone;
    private PaymentMethodEnum paymentMethod;
    private String note;
    private List<OrderDetailRequestDTO> orderDetails;
}
