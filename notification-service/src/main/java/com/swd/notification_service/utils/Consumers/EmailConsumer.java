package com.swd.notification_service.utils.Consumers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swd.notification_service.services.EmailAdminService;
import com.swd.notification_service.services.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {
    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final EmailAdminService emailAdminService;

    public EmailConsumer(EmailService emailService, EmailAdminService emailAdminService) {
        this.emailService = emailService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        this.emailAdminService = emailAdminService;
    }

    @KafkaListener(topics = "email_register_account_topic", groupId = "emailMessageTopic")
    public void sendVerificationEmail(String accountJson) {
        try {
            System.out.println("Received account JSON: " + accountJson);
            emailService.sendVerifyAccountMailTemplate(accountJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "email_forgot_password_topic", groupId = "emailMessageTopic")
    public void forgotPassword(String account) {
        try {
            System.out.println("Received account: " + account);
            emailService.sendForgotPasswordEmail(account);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "email_change-topic", groupId = "emailMessageTopic")
    public void changeEmail(String account) {
        try {
            System.out.println("Received account: " + account);
            emailService.changeEmail(account);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}