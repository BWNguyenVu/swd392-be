package com.example.myflower.mapper;

import com.example.myflower.dto.payment.responses.PaymentResponseDTO;
import com.example.myflower.entity.Payment;

public class PaymentMapper {
    public static PaymentResponseDTO buildPaymentResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .note(payment.getNote())
                .orderCode(payment.getOrderCode().intValue())
                .checkoutUrl(payment.getCheckoutUrl())
                .paymentLinkId(payment.getPaymentLinkId())
                .createAt(payment.getCreatedAt())
                .build();
    }
}
