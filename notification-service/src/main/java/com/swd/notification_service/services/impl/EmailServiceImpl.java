package com.swd.notification_service.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swd.notification_service.dto.account.Account;
import com.swd.notification_service.dto.orders.OrderDetailResponseDTO;
import com.swd.notification_service.dto.orders.OrderResponseDTO;
import com.swd.notification_service.dto.email_detail.EmailDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl {

    private final ObjectMapper objectMapper;
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    @Value("${public.api.url}")
    private String publicApiUrl;

    @Autowired
    public EmailServiceImpl(ObjectMapper objectMapper, TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        this.objectMapper = objectMapper;
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    public void sendVerifyAccountMailTemplate(String account) {
        try {
            Account accountDTO = objectMapper.readValue(account, Account.class);

            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setName(accountDTO.getName());
            emailDetail.setRecipient(accountDTO.getEmail());
            emailDetail.setSubject("Congratulations!");
            emailDetail.setMsgBody("Your account has been verified!");

            VerifyAccountMailTemplate(emailDetail, accountDTO);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle specific exceptions or log appropriately
        }
    }

    public void changeEmail(String account) {
        try {
            Account accountDTO = objectMapper.readValue(account, Account.class);

            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setName(accountDTO.getName());
            emailDetail.setRecipient(accountDTO.getEmail());
            emailDetail.setSubject("Change Email Request");

            String otp = accountDTO.getOtp();
            emailDetail.setMsgBody(otp);
            sendEmailWithTemplate(emailDetail, "ChangePasswordEmailTemplate");
        } catch (Exception e) {
            e.printStackTrace();
            // Handle specific exceptions or log appropriately
        }
    }
    public void sendForgotPasswordEmail(String account) {
        try {
            Account accountDTO = objectMapper.readValue(account, Account.class);

            String token = accountDTO.getTokens();
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setName(accountDTO.getName());
            emailDetail.setRecipient(accountDTO.getEmail());
            emailDetail.setSubject("Forgot Password Request");

            String link = "localhost:4200/reset-password?token=" + token;
            emailDetail.setMsgBody(link);
            sendEmailWithTemplate(emailDetail, "ForgotPasswordEmailTemplate");
        } catch (Exception e) {
            e.printStackTrace();
            // Handle specific exceptions or log appropriately
        }
    }


    public void VerifyAccountMailTemplate(EmailDetail emailDetail, Account account) {
        try {
            Context context = new Context();
            context.setVariable("name", emailDetail.getName());

            String token = account.getTokens();
            String link = "http://localhost:4200?token=" + token;
            context.setVariable("link", link);

            String text = templateEngine.process("sendVerifyEmail", context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom("isolutions.top.contact@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getRecipient());
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            mimeMessageHelper.setText(text, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Handle email sending exceptions
        }
    }

    public void buyerOrderByWallet(String order){
        try {
            OrderResponseDTO orderResponseDTO = objectMapper.readValue(order, OrderResponseDTO.class);
            EmailDetail emailDetail = createEmailDetailOrder(orderResponseDTO, "Thank You for Your Purchase!", "");
            sendOrderConfirmationEmail(emailDetail, orderResponseDTO, "OrderSuccessEmail");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void orderNotificationForSeller(String order) {
        try {
            OrderResponseDTO orderResponseDTO = objectMapper.readValue(order, OrderResponseDTO.class);
            orderResponseDTO.getOrderDetails().stream()
                    .distinct()
                    .forEach(orderDetailResponseDTO ->
                            sendNewOrderForSeller(
                                    createEmailDetailOrderForSeller(orderDetailResponseDTO, "You have a new order", ""),
                                    orderDetailResponseDTO,
                                    "OrderSuccessSellerEmail"
                            )
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private EmailDetail createEmailDetailOrderForSeller(OrderDetailResponseDTO orderDetailResponseDTO, String subject, String msgBody) {
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setName(orderDetailResponseDTO.getFlowerListing().getUser().getName());
        emailDetail.setRecipient(orderDetailResponseDTO.getFlowerListing().getUser().getEmail());
        emailDetail.setSubject(subject);
        emailDetail.setMsgBody(msgBody);
        return emailDetail;
    }

    private EmailDetail createEmailDetailOrder(OrderResponseDTO orderResponseDTO, String subject, String msgBody) {
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setName(orderResponseDTO.getBuyerName());
        emailDetail.setRecipient(orderResponseDTO.getBuyerEmail());
        emailDetail.setSubject(subject);
        emailDetail.setMsgBody(msgBody);
        return emailDetail;
    }


    public void sendEmailWithTemplate(EmailDetail emailDetail, String templateName) {
        try {
            Context context = new Context();
            context.setVariable("name", emailDetail.getName());
            context.setVariable("link", emailDetail.getMsgBody());
            context.setVariable("otp", emailDetail.getMsgBody());
            String text = templateEngine.process(templateName, context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom("BLOSSOM APP <isolutions.top.contact@gmail.com>");
            mimeMessageHelper.setTo(emailDetail.getRecipient());
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            mimeMessageHelper.setText(text, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Handle email sending exceptions
        }
    }
    private void sendNewOrderForSeller(EmailDetail emailDetail, OrderDetailResponseDTO orderDetailResponseDTO, String templateName) {
        try {
            Context context = new Context();

            // Set basic buyer and order information
            context.setVariable("buyerName", emailDetail.getName());
            context.setVariable("invoiceNumber", orderDetailResponseDTO.getId());
            context.setVariable("total", orderDetailResponseDTO.getPrice());
            context.setVariable("transactionsLink", "https://blossom.isolutions.top/orders-history");
            context.setVariable("name", orderDetailResponseDTO.getFlowerListing().getName());
            context.setVariable("quantity", orderDetailResponseDTO.getQuantity());
            context.setVariable("imageUrl", orderDetailResponseDTO.getFlowerListing().getImages().get(0).getUrl());

            // Render the template with context
            String text = templateEngine.process(templateName, context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom("BLOSSOM APP <isolutions.top.contact@gmail.com>");
            mimeMessageHelper.setTo(emailDetail.getRecipient());
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            mimeMessageHelper.setText(text, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    private void sendOrderConfirmationEmail(EmailDetail emailDetail, OrderResponseDTO orderResponseDTO, String templateName) {
        try {
            Context context = new Context();

            // Set basic buyer and order information
            context.setVariable("buyerName", emailDetail.getName());
            context.setVariable("invoiceNumber", orderResponseDTO.getId());
            context.setVariable("total", orderResponseDTO.getTotalAmount());
            context.setVariable("transactionsLink", "https://blossom.isolutions.top/orders-history");

            // Prepare order details list to match the template's structure
            List<Map<String, Object>> productList = new ArrayList<>();
            for (OrderDetailResponseDTO detail : orderResponseDTO.getOrderDetails()) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("name", detail.getFlowerListing().getName());
                productData.put("quantity", detail.getQuantity());
                productData.put("price", detail.getPrice());
                productData.put("imageUrl", detail.getFlowerListing().getImages().get(0).getUrl()); // Assuming first image is used
                productList.add(productData);
            }
            context.setVariable("orderDetails", productList);

            // Render the template with context
            String text = templateEngine.process(templateName, context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom("BLOSSOM APP <isolutions.top.contact@gmail.com>");
            mimeMessageHelper.setTo(emailDetail.getRecipient());
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            mimeMessageHelper.setText(text, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }



    }



    // Example method to calculate subtotal if it's not directly provided

}
