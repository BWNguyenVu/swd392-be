package com.example.myflower.service;

import com.example.myflower.dto.payment.requests.CreatePaymentRequestDTO;
import com.example.myflower.dto.payment.responses.CreatePaymentResponseDTO;

public interface PaymentService {
    CreatePaymentResponseDTO createPayment(CreatePaymentRequestDTO createPaymentRequestDTO);
}
