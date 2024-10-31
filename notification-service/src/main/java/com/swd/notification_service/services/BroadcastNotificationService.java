package com.swd.notification_service.services;

import com.swd.notification_service.dto.broadcast_notification.request.CreateBroadcastNotificationRequestDTO;
import com.swd.notification_service.dto.broadcast_notification.request.GetBroadcastNotificationListRequestDTO;
import com.swd.notification_service.dto.broadcast_notification.response.BroadcastNotificationResponseDTO;
import com.swd.notification_service.dto.pagination.response.PaginationResponseDTO;
import org.springframework.transaction.annotation.Transactional;

public interface BroadcastNotificationService {
    BroadcastNotificationResponseDTO createBroadcastNotification(CreateBroadcastNotificationRequestDTO requestDTO);

    PaginationResponseDTO<BroadcastNotificationResponseDTO> getBroadcastNotifications(GetBroadcastNotificationListRequestDTO requestDTO);

    BroadcastNotificationResponseDTO getBroadcastNotificationById(Integer notificationId);
    void deleteBroadcastNotification(Integer notificationId);

    void restoreBroadcastNotification(Integer notificationId);

    @Transactional
    void checkForBroadcastNotification();
}
