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
    private boolean status;
    private LocalDateTime createdAt;
}
