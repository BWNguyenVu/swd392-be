package com.swd.notification_service.dto.notifications;

import com.swd.notification_service.entity.enumType.DestinationScreenEnum;
import com.swd.notification_service.entity.enumType.NotificationTypeEnum;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NotificationResponseDTO {
    private Integer id;
    private Integer userId;
    private String title;
    private String message;
    private NotificationTypeEnum type;
    private DestinationScreenEnum destinationScreen;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
