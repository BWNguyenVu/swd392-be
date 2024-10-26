package com.swd.notification_service.services;

import com.swd.notification_service.dto.notifications.NotificationResponseDTO;
import org.springframework.messaging.handler.annotation.Payload;

public interface SocketService {
    void sendNotificationToUser(@Payload NotificationResponseDTO responseDTO);
}
