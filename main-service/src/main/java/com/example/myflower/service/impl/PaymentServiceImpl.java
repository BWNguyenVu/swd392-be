package com.example.myflower.service.impl;

import com.example.myflower.dto.payment.requests.CreatePaymentRequestDTO;
import com.example.myflower.dto.payment.responses.PaymentResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.Payment;
import com.example.myflower.entity.enumType.WalletLogStatusEnum;
import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import com.example.myflower.mapper.PaymentMapper;
import com.example.myflower.repository.PaymentRepository;
import com.example.myflower.service.AccountService;
import com.example.myflower.service.PaymentService;
import com.example.myflower.service.WalletLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Value("http://" + "${public.api.url}" + ":6868/")
    private String returnUrl;
    @Value("http://" + "${public.api.url}" + ":6868/")
    private String cancelUrl;
    @Autowired
    private PayOS payOS;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private WalletLogService walletLogService;
    @Lazy
    @Autowired
    private AccountService accountService;
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public PaymentResponseDTO createPayment(CreatePaymentRequestDTO createPaymentRequestDTO, Account account) {
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
            Payment payment = Payment.builder()
                    .paymentLinkId(data.getPaymentLinkId())
                    .checkoutUrl(data.getCheckoutUrl())
                    .currency(data.getCurrency())
                    .user(account)
                    .note(data.getDescription())
                    .amount(BigDecimal.valueOf(data.getAmount()))
                    .orderCode(data.getOrderCode())
                    .createdAt(LocalDateTime.now())
                    .build();
            paymentRepository.save(payment);
            return PaymentMapper.buildPaymentResponseDTO(payment);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create payment link: " + e.getMessage());
        }
    }

    public void payosTransferHandler(ObjectNode body) throws JsonProcessingException, IllegalArgumentException{
        String code = body.get("code").asText();
        String dataCode = body.has("data") && body.get("data").has("code") ? body.get("data").get("code").asText() : null;
        String dataDesc = body.has("data") && body.get("data").has("desc") ? body.get("data").get("desc").asText() : null;

        boolean success = "00".equals(code) || ("00".equals(dataCode) && "Thành công".equals(dataDesc));

        body.put("success", success);

        Webhook webhookBody = objectMapper.treeToValue(body, Webhook.class);
        try {
            WebhookData data = payOS.verifyPaymentWebhookData(webhookBody);
            Payment payment = paymentRepository.findByPaymentLinkId(data.getPaymentLinkId());
            WalletLogStatusEnum status = data.getCode().equals("00") ? WalletLogStatusEnum.SUCCESS : WalletLogStatusEnum.FAILED;
            if (status == WalletLogStatusEnum.SUCCESS) {
                accountService.handleBalanceByOrder(
                        payment.getUser(),
                        BigDecimal.valueOf(data.getAmount()),
                        WalletLogTypeEnum.DEPOSIT,
                        null,
                        null,
                        payment,
                        WalletLogStatusEnum.SUCCESS,
                        false
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify payment webhook data: " + e.getMessage());
        }
    }

    public PaymentResponseDTO getPaymentById(Integer id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        return PaymentMapper.buildPaymentResponseDTO(payment.get());
    }
}
