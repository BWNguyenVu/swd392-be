package com.example.myflower.dto.notification;

import com.example.myflower.entity.enumType.DestinationScreenEnum;
import com.example.myflower.entity.enumType.NotificationTypeEnum;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NotificationMessageDTO {
    private Integer userId;
    private String title;
    private String message;
    private NotificationTypeEnum type;
    private DestinationScreenEnum destinationScreen;
}
