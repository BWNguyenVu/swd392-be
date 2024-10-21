package com.swd.notification_service.config;

import com.swd.notification_service.dto.account.Account;
import com.swd.notification_service.dto.notifications.PushNotificationEventDTO;
import com.swd.notification_service.dto.wallets.CashOutResponse;
import com.swd.notification_service.dto.wallets.DataResponse;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<String, DataResponse> cashOutResponseConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "cashoutResponseTopic");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName());
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.ojt.notification_service.dto.wallets, *, com.ojt.notification_service.dto.wallets.Enum.*, com.ojt.notification_service.dto.wallets.*, com.ojt.notification_service.dto.account, com.ojt.notification_service.dto.account.*, com.ojt.mockproject.entity.*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(DataResponse.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DataResponse> cashOutResponseKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DataResponse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cashOutResponseConsumerFactory());
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // Optional: if you want manual acknowledgment
        return factory;
    }
}
