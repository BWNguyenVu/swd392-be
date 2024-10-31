package com.swd.notification_service.entity;

import com.swd.notification_service.entity.enumType.DestinationScreenEnum;
import com.swd.notification_service.entity.enumType.NotificationTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = true)
    private String message;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum type;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DestinationScreenEnum destinationScreen;
    @Column(nullable = true)
    private LocalDateTime scheduledTime;
    @Column(name = "is_executed", nullable = true)
    private Boolean isExecuted;
    @Column(nullable = true)
    private LocalDateTime executeTime;
    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;
    @Column(name = "is_deleted", nullable = true)
    private Boolean isDeleted;

    @PrePersist
    protected void onCreate() {
        this.isDeleted = false;
        this.isExecuted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
