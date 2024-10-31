package com.swd.notification_service.repository;

import com.swd.notification_service.entity.BroadcastNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BroadcastNotificationRepository extends JpaRepository<BroadcastNotification, Integer> {
    List<BroadcastNotification> findByIsExecutedFalseAndScheduledTimeAfter(LocalDateTime currentDate);

    @Query("SELECT bn FROM BroadcastNotification bn " +
            "WHERE (:search IS NULL OR bn.title ILIKE %:search% OR bn.message ILIKE %:search%) " +
            "AND (bn.executeTime BETWEEN :startDate AND :endDate) " +
            "AND (:isExecuted IS NULL OR bn.isExecuted = :isExecuted) " +
            "AND (:isDeleted IS NULL OR bn.isDeleted = :isDeleted)")
    Page<BroadcastNotification> findByParameters(
            @Param("search") String search,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("isExecuted") Boolean isExecuted,
            @Param("isDeleted") Boolean isDeleted,
            Pageable pageable
    );
}
