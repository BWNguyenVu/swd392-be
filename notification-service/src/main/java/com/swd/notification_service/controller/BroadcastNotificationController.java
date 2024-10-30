package com.swd.notification_service.controller;

import com.swd.notification_service.dto.broadcast_notification.request.CreateBroadcastNotificationRequestDTO;
import com.swd.notification_service.dto.broadcast_notification.response.BroadcastNotificationResponseDTO;
import com.swd.notification_service.services.BroadcastNotificationService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/broadcast-notifications")
@CrossOrigin("**")
@RequiredArgsConstructor
public class BroadcastNotificationController {
    @NonNull
    private BroadcastNotificationService broadcastNotificationService;

    @PostMapping
    public ResponseEntity<BroadcastNotificationResponseDTO> createBroadcastNotification(@Valid @RequestBody CreateBroadcastNotificationRequestDTO requestDTO) {
        return ResponseEntity.ok().body(broadcastNotificationService.createBroadcastNotification(requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBroadcastNotification(@PathVariable Integer id) {
        broadcastNotificationService.deleteBroadcastNotification(id);
        return ResponseEntity.noContent().build();
    }
}
