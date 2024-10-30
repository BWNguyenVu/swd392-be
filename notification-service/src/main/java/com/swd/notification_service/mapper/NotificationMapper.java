package com.swd.notification_service.mapper;

import com.swd.notification_service.dto.notifications.NotificationResponseDTO;
import com.swd.notification_service.entity.Notification;

public class NotificationMapper {
    private NotificationMapper() {}
    public static NotificationResponseDTO toResponseDTO(Notification notification) {
        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .destinationScreen(notification.getDestinationScreen())
                .isRead(notification.getIsRead())
                .isDeleted(notification.getIsDeleted())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}
