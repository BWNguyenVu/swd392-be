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
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private Integer userId;
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
    @Column(name = "is_read")
    private Boolean isRead;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.isRead = false;
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
