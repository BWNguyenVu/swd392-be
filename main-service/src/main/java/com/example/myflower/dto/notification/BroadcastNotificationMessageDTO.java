package com.example.myflower.dto.notification;

import com.example.myflower.entity.enumType.DestinationScreenEnum;
import com.example.myflower.entity.enumType.NotificationTypeEnum;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastNotificationMessageDTO implements Serializable {
    private String title;
    private String message;
    private NotificationTypeEnum type;
    private DestinationScreenEnum destinationScreen;
    private LocalDateTime scheduledTime;
}
