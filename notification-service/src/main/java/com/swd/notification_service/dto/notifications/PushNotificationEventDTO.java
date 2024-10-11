package com.swd.notification_service.dto.notifications;

import com.swd.notification_service.entity.enumType.DestinationScreenEnum;
import com.swd.notification_service.entity.enumType.NotificationTypeEnum;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PushNotificationEventDTO {
    private Integer id;
    private Integer userId;
    private String title;
    private String message;
    private NotificationTypeEnum type;
    private DestinationScreenEnum destinationScreen;
}
