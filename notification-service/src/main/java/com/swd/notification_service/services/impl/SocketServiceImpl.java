package com.swd.notification_service.services.impl;

import com.swd.notification_service.dto.notifications.NotificationResponseDTO;
import com.swd.notification_service.services.SocketService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocketServiceImpl implements SocketService {
    @NonNull
    private SimpMessagingTemplate template;

    @Override
    public void sendNotificationToUser(@Payload NotificationResponseDTO responseDTO) {
        template.convertAndSendToUser(responseDTO.getUserId().toString(),"/queue/messages", responseDTO);
    }
}
