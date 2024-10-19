package com.swd.notification_service.services;

import com.swd.notification_service.dto.notifications.NotificationResponseDTO;

import java.util.List;

public interface NotificationService {
    List<NotificationResponseDTO> getAllNotifications(Integer userId);

    void pushNotification(String notification);

    void readAllNotifications(Integer userId);
}
