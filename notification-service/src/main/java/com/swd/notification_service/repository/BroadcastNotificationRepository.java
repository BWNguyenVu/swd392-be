package com.swd.notification_service.repository;

import com.swd.notification_service.entity.BroadcastNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BroadcastNotificationRepository extends JpaRepository<BroadcastNotification, Integer> {
    List<BroadcastNotification> findByIsExecutedFalseAndScheduledTimeAfter(LocalDateTime currentDate);
}
