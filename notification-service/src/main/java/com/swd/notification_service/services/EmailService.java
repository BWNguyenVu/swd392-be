package com.swd.notification_service.services;

import com.swd.notification_service.dto.account.Account;
import com.swd.notification_service.dto.orders.OrderDetailResponseDTO;
import com.swd.notification_service.dto.orders.OrderResponseDTO;
import com.swd.notification_service.dto.email_detail.EmailDetail;

public interface EmailService {

    void sendVerifyAccountMailTemplate(String account);

    void changeEmail(String account);

    void sendForgotPasswordEmail(String account);

    void VerifyAccountMailTemplate(EmailDetail emailDetail, Account account);

    void buyerOrderByWallet(String order);

    void orderNotificationForSeller(String order);

    void sendEmailWithTemplate(EmailDetail emailDetail, String templateName);
}
