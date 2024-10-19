package com.swd.notification_service.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swd.notification_service.dto.notifications.NotificationResponseDTO;
import com.swd.notification_service.dto.notifications.PushNotificationEventDTO;
import com.swd.notification_service.entity.Notification;
import com.swd.notification_service.mapper.NotificationMapper;
import com.swd.notification_service.repository.NotificationRepository;
import com.swd.notification_service.services.NotificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    @NonNull
    ObjectMapper objectMapper;
    @NonNull
    NotificationRepository notificationRepository;

    @Override
    public List<NotificationResponseDTO> getAllNotifications(Integer userId) {
        List<Notification> result = notificationRepository.getAllByUserId(userId);
        return result.stream()
                .map(NotificationMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void pushNotification(String notification) {
        try {
            PushNotificationEventDTO eventDTO = objectMapper.readValue(notification, PushNotificationEventDTO.class);
            Notification notificationEntity = Notification.builder()
                    .userId(eventDTO.getUserId())
                    .title(eventDTO.getTitle())
                    .type(eventDTO.getType())
                    .message(eventDTO.getMessage())
                    .destinationScreen(eventDTO.getDestinationScreen())
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notificationEntity);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readAllNotifications(Integer userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
}
