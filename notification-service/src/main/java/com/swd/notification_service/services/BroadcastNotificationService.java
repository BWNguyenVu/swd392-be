package com.swd.notification_service.services;

import com.swd.notification_service.dto.broadcast_notification.request.CreateBroadcastNotificationRequestDTO;
import com.swd.notification_service.dto.broadcast_notification.response.BroadcastNotificationResponseDTO;
import org.springframework.transaction.annotation.Transactional;

public interface BroadcastNotificationService {
    BroadcastNotificationResponseDTO createBroadcastNotification(CreateBroadcastNotificationRequestDTO requestDTO);
    BroadcastNotificationResponseDTO getBroadcastNotification(Integer notificationId);
    void deleteBroadcastNotification(Integer notificationId);

    @Transactional
    void checkForBroadcastNotification();
}
