package com.example.myflower.service.impl;

import com.example.myflower.dto.payment.requests.CreatePaymentRequestDTO;
import com.example.myflower.dto.payment.responses.CreatePaymentResponseDTO;
import com.example.myflower.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class PaymentServiceImpl implements PaymentService {
    final String returnUrl = "http://localhost:3000/";
    final String cancelUrl = "http://localhost:3000/";

    @Autowired
    private PayOS payOS;

    @Override
    public CreatePaymentResponseDTO createPayment(CreatePaymentRequestDTO createPaymentRequestDTO) {
        String currentTimeString = String.valueOf(new Date().getTime());
        long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));

        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .description(createPaymentRequestDTO.getNote())
                .amount(createPaymentRequestDTO.getTotalAmount().intValue())
                .item(createPaymentRequestDTO.getItem())
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .build();

        CheckoutResponseData data;
        try {
            data = payOS.createPaymentLink(paymentData);
            return new CreatePaymentResponseDTO(
                BigDecimal.valueOf(data.getAmount()),
                    data.getCurrency(),
                    data.getDescription(),
                    paymentData.getOrderCode().intValue(),
                    data.getCheckoutUrl(),
                    data.getQrCode()
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to create payment link: " + e.getMessage());
        }
    }
}
