package com.swd.notification_service.controller;

import com.swd.notification_service.dto.notifications.NotificationResponseDTO;
import com.swd.notification_service.services.NotificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@CrossOrigin("**")
@RequiredArgsConstructor
public class NotificationController {
    @NonNull
    private NotificationService notificationService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotificationsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok().body(notificationService.getAllNotifications(userId));
    }

    @PostMapping("/users/{userId}/read-all")
    public ResponseEntity<Void> readAllNotifications(@PathVariable Integer userId) {
        notificationService.readAllNotifications(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificationById(@PathVariable Integer id) {
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteAllNotificationsByUser(@PathVariable Integer userId) {
        return ResponseEntity.noContent().build();
    }
}
