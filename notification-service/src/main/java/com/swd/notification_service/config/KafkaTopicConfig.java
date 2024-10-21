package com.swd.notification_service.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    //hello kafka config
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic emailMessageTopic() {
        return new NewTopic("emailMessageTopic", 1, (short) 1);
    }

    @Bean
    public NewTopic notificationMessageTopic() {
        return new NewTopic("notificationsTopic", 1, (short) 1);
    }

    @Bean
    public NewTopic processPaymentMessage() {
        return new NewTopic("create-payment", 1, (short) 1);
    }
}