package com.integration_service.utils.consumers;

import com.integration_service.service.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AccountIntegrationConsumer {
    @Autowired
    private IntegrationService integrationService;

    @KafkaListener(topics = "create_account-integration_topic", groupId = "accountIntegrationTopic")
    public void changeEmail(String account) {
        try {
            System.out.println("Received account: " + account);
            integrationService.createAccountIntegration(account);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
