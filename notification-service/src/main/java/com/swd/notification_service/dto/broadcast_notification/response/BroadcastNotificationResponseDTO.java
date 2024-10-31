package com.swd.notification_service.dto.broadcast_notification.response;

import com.swd.notification_service.entity.enumType.DestinationScreenEnum;
import com.swd.notification_service.entity.enumType.NotificationTypeEnum;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastNotificationResponseDTO {
    private Integer id;
    private String title;
    private String message;
    private NotificationTypeEnum type;
    private DestinationScreenEnum destinationScreen;
    private LocalDateTime scheduledTime;
    private Boolean isExecuted;
    private LocalDateTime executeTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
