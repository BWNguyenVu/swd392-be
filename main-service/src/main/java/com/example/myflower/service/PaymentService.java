package com.example.myflower.service;

import com.example.myflower.dto.payment.requests.CreatePaymentRequestDTO;
import com.example.myflower.dto.payment.responses.CreatePaymentResponseDTO;
import com.example.myflower.entity.Account;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import vn.payos.type.WebhookData;

public interface PaymentService {
    CreatePaymentResponseDTO createPayment(CreatePaymentRequestDTO createPaymentRequestDTO, Account account);
    void payosTransferHandler(ObjectNode body) throws JsonProcessingException, IllegalArgumentException;
}
