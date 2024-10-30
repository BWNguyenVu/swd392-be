package com.swd.notification_service.mapper;

import com.swd.notification_service.dto.broadcast_notification.response.BroadcastNotificationResponseDTO;
import com.swd.notification_service.entity.BroadcastNotification;

public class BroadcastNotificationMapper {
    private BroadcastNotificationMapper() {}
    public static BroadcastNotificationResponseDTO toResponseDTO(final BroadcastNotification entity) {
        if (entity == null) {
            return null;
        }
        return BroadcastNotificationResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .type(entity.getType())
                .executeTime(entity.getExecuteTime())
                .isExecuted(entity.getIsExecuted())
                .scheduledTime(entity.getScheduledTime())
                .destinationScreen(entity.getDestinationScreen())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .message(entity.getMessage())
                .build();
    }
}
