package com.example.myflower.service;

import com.example.myflower.dto.payment.requests.CreatePaymentResponseDTO;
import com.example.myflower.dto.payment.responses.PaymentResponseDTO;
import com.example.myflower.entity.Account;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface PaymentService {
    PaymentResponseDTO createPayment(CreatePaymentResponseDTO createPaymentResponseDTO, Account account);
    void payosTransferHandler(ObjectNode body) throws JsonProcessingException, IllegalArgumentException;
    PaymentResponseDTO getPaymentById(Integer id);
}
