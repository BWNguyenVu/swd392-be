package com.swd.notification_service.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swd.notification_service.dto.notifications.NotificationResponseDTO;
import com.swd.notification_service.dto.notifications.PushNotificationEventDTO;
import com.swd.notification_service.entity.Notification;
import com.swd.notification_service.mapper.NotificationMapper;
import com.swd.notification_service.repository.NotificationRepository;
import com.swd.notification_service.services.NotificationService;
import com.swd.notification_service.services.SocketService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    @NonNull
    private ObjectMapper objectMapper;
    @NonNull
    private NotificationRepository notificationRepository;
    @NonNull
    private SocketService socketService;

    @Override
    public List<NotificationResponseDTO> getAllNotifications(Integer userId) {
        List<Notification> result = notificationRepository.getAllByUserId(userId);
        return result.stream()
                .map(NotificationMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void pushNotification(PushNotificationEventDTO eventDTO) {
        try {
            Notification notificationEntity = Notification.builder()
                    .userId(eventDTO.getUserId())
                    .title(eventDTO.getTitle())
                    .type(eventDTO.getType())
                    .message(eventDTO.getMessage())
                    .destinationScreen(eventDTO.getDestinationScreen())
                    .build();
            Notification result = notificationRepository.save(notificationEntity);

            socketService.sendNotificationToUser(NotificationMapper.toResponseDTO(result));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushMultipleNotifications(List<PushNotificationEventDTO> eventDTOList) {
        try {
            List<Notification> notificationList = eventDTOList.stream()
                    .map(eventDTO -> Notification.builder()
                            .userId(eventDTO.getUserId())
                            .title(eventDTO.getTitle())
                            .type(eventDTO.getType())
                            .message(eventDTO.getMessage())
                            .destinationScreen(eventDTO.getDestinationScreen())
                            .build())
                    .toList();
            List<Notification> result = notificationRepository.saveAll(notificationList);
            for (Notification notification : result) {
                socketService.sendNotificationToUser(NotificationMapper.toResponseDTO(notification));
            }
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
