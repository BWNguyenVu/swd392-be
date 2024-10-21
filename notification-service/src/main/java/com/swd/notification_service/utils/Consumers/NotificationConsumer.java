package com.swd.notification_service.utils.Consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swd.notification_service.dto.notifications.PushNotificationEventDTO;
import com.swd.notification_service.services.NotificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    @NonNull
    private final NotificationService notificationService;
    @NonNull
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "push_notification_topic", groupId = "notificationsTopic")
    public void processPushNotification(String eventJson) {
        notificationService.pushNotification(eventJson);
    }
}
