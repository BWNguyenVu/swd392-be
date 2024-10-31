package com.swd.notification_service.services;

import com.swd.notification_service.dto.notifications.NotificationResponseDTO;
import com.swd.notification_service.dto.notifications.PushNotificationEventDTO;

import java.util.List;

public interface NotificationService {
    List<NotificationResponseDTO> getAllNotifications(Integer userId);

    void pushNotification(PushNotificationEventDTO eventDTO);

    void pushMultipleNotifications(List<PushNotificationEventDTO> eventDTOList);

    void readAllNotifications(Integer userId);
}
