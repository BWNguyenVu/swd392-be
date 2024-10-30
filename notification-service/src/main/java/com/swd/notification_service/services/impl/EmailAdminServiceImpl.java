package com.swd.notification_service.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swd.notification_service.dto.email_detail.EmailDetail;
import com.swd.notification_service.dto.wallets.CashoutAdminResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Service
public class EmailAdminServiceImpl {
    private final ObjectMapper objectMapper;
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    @Value("${public.api.url}")
    private String publicApiUrl;

    @Autowired
    public EmailAdminServiceImpl(ObjectMapper objectMapper, TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        this.objectMapper = objectMapper;
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    public void cashoutAdmin(String cashoutAdmin) {
        try {
            CashoutAdminResponse cashoutAdminDTO = objectMapper.readValue(cashoutAdmin, CashoutAdminResponse.class);

            String[] emailArray = extractEmails(cashoutAdminDTO.getEmailAdmin());

            for (String email : emailArray) {
                if (isValidEmailAddress(email.trim())) { // Trim to remove any extra whitespace
                    EmailDetail emailDetail = new EmailDetail();
                    emailDetail.setName("Admin");
                    emailDetail.setRecipient(email.trim());
                    emailDetail.setSubject("Cashout Initiated by Instructor");

                    String emailTemplate = createEmailTemplate(cashoutAdminDTO);
                    emailDetail.setMsgBody(emailTemplate);

                    sendEmail(emailDetail);
                } else {
                    // Handle invalid email address format
                    System.err.println("Invalid email address format: " + email);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Handle specific exceptions or log appropriately
        }
    }

    private String[] extractEmails(String emails) {
        // Extract email addresses from the input string in the format "[email1, email2, ...]"
        // Assuming the input string format is consistent
        String[] emailArray = emails.replaceAll("\\[|\\]", "").split(", ");
        return emailArray;
    }
    private boolean isValidEmailAddress(String email) {
        // Perform basic email format validation
        // You can use a more sophisticated regex or library for thorough validation
        return email != null && email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }
    private String createEmailTemplate(CashoutAdminResponse cashoutDTO) {
        String template = "Hello Admin,\n\n" +
                "An instructor has initiated a cashout request.\n\n" +
                "Here are the details:\n" +
                "Instructor Name: %s\n" +
                "Remaining Balance: %s\n" +
                "Cashout Amount: %s\n" +
                "Bank Name: %s\n" +
                "Bank Account Number: %s\n\n" +
                "Please take necessary actions.\n\n" +
                "Thank you,\n" +
                "Your Application Team";

        return String.format(template,
                cashoutDTO.getDataResponse().getName(),
                cashoutDTO.getDataResponse().getRemainingBalance(),
                cashoutDTO.getDataResponse().getCashoutBalance(),
                cashoutDTO.getDataResponse().getBankName(),
                cashoutDTO.getDataResponse().getBankAccountNumber());
    }

    private void sendEmail(EmailDetail emailDetail) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom("contact.us.nicetrip@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getRecipient());
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            mimeMessageHelper.setText(emailDetail.getMsgBody(), false);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
