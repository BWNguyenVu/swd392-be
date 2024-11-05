package com.swd.notification_service.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swd.notification_service.dto.notifications.NotificationCountResponseDTO;
import com.swd.notification_service.dto.notifications.NotificationResponseDTO;
import com.swd.notification_service.dto.notifications.PushNotificationEventDTO;
import com.swd.notification_service.dto.pagination.CursorPaginationRequest;
import com.swd.notification_service.entity.Notification;
import com.swd.notification_service.mapper.NotificationMapper;
import com.swd.notification_service.repository.NotificationRepository;
import com.swd.notification_service.services.NotificationService;
import com.swd.notification_service.services.SocketService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public List<NotificationResponseDTO> getAllNotifications(Integer userId, CursorPaginationRequest<LocalDateTime> paginationRequest) {
        if (paginationRequest.getCursor() == null) {
            paginationRequest.setCursor(LocalDateTime.now());
        }
        Pageable pageable = PageRequest.of(0, paginationRequest.getSize());
        Page<Notification> result = notificationRepository.findByUserIdAndCreatedAtBeforeOrderByCreatedAtDesc(userId, paginationRequest.getCursor(), pageable);
        return result.stream()
                .map(NotificationMapper::toResponseDTO)
                .toList();
    }

    @Override
    public NotificationCountResponseDTO getUnreadNotificationCount(Integer userId) {
        return NotificationCountResponseDTO.builder()
                .count(notificationRepository.countUnreadByUserId(userId))
                .build();
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
