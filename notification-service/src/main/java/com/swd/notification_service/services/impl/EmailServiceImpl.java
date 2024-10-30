package com.swd.notification_service.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swd.notification_service.dto.account.Account;
import com.swd.notification_service.dto.email_detail.EmailDetail;
import com.swd.notification_service.dto.orders.OrderDTO;
import com.swd.notification_service.dto.wallets.DataResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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

    public void buyCourseSuccessByWallet(String order) {
        try {
            OrderDTO orderDTO = objectMapper.readValue(order, OrderDTO.class);

            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setName(orderDTO.getBuyerName());
            emailDetail.setRecipient(orderDTO.getBuyerEmail());
            emailDetail.setSubject("Course Purchase Success!");

            Context context = new Context();
            context.setVariable("name", orderDTO.getBuyerName());
//            context.setVariable("courseName", orderDTO.getCourseName()); // Assuming OrderDTO has a courseName field
            context.setVariable("totalPrice", orderDTO.getTotalPrice());

            String emailContent = templateEngine.process("CoursePurchaseSuccessEmailTemplate", context);
            emailDetail.setMsgBody(emailContent);
            sendEmailWithTemplate(emailDetail, "CoursePurchaseSuccessEmailTemplate");

        } catch (Exception e) {
            e.printStackTrace();
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

            String link = "http://103.250.78.50:6868/api/v1/auth/reset-password?token=" + token;
            emailDetail.setMsgBody(link);
            sendEmailWithTemplate(emailDetail, "ForgotPasswordEmailTemplate");
        } catch (Exception e) {
            e.printStackTrace();
            // Handle specific exceptions or log appropriately
        }
    }

    public void cashoutByInstructor(String cashout) {
        try {
            DataResponse cashoutDTO = objectMapper.readValue(cashout, DataResponse.class);

            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setName(cashoutDTO.getName());
            emailDetail.setRecipient(cashoutDTO.getEmail());
            emailDetail.setSubject("Congratulations on Your Cashout!");

            String emailTemplate = createEmailTemplate(cashoutDTO);
            emailDetail.setMsgBody(emailTemplate);

            sendEmail(emailDetail);

        } catch (Exception e) {
            e.printStackTrace();
            // Handle specific exceptions or log appropriately
        }
    }

    private String createEmailTemplate(DataResponse cashoutDTO) {
        String template = "Hello %s,\n\n" +
                "Congratulations on your cashout!\n\n" +
                "Here are the details:\n" +
                "Remaining Balance: %s\n" +
                "Cashout Amount: %s\n" +
                "Bank Name: %s\n" +
                "Bank Account Number: %s\n\n" +
                "Thank you,\n" +
                "Your Application Team";

        return String.format(template,
                cashoutDTO.getName(),
                cashoutDTO.getRemainingBalance(),
                cashoutDTO.getCashoutBalance(),
                cashoutDTO.getBankName(),
                cashoutDTO.getBankAccountNumber());
    }

    private void sendEmail(EmailDetail emailDetail) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom("isolutions.top.contact@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getRecipient());
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            mimeMessageHelper.setText(emailDetail.getMsgBody(), false);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void VerifyAccountMailTemplate(EmailDetail emailDetail, Account account) {
        try {
            Context context = new Context();
            context.setVariable("name", emailDetail.getName());

            String token = account.getTokens();
            String link = "http://103.250.78.50:6868/api/v1/auth/verify/" + token;
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

    public void sendEmailWithTemplate(EmailDetail emailDetail, String templateName) {
        try {
            Context context = new Context();
            context.setVariable("name", emailDetail.getName());
            context.setVariable("link", emailDetail.getMsgBody());
            context.setVariable("otp", emailDetail.getMsgBody());

            String text = templateEngine.process(templateName, context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

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
}
