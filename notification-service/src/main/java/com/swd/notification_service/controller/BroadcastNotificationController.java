package com.swd.notification_service.controller;

import com.swd.notification_service.dto.broadcast_notification.request.CreateBroadcastNotificationRequestDTO;
import com.swd.notification_service.dto.broadcast_notification.request.GetBroadcastNotificationListRequestDTO;
import com.swd.notification_service.dto.broadcast_notification.response.BroadcastNotificationResponseDTO;
import com.swd.notification_service.dto.pagination.response.PaginationResponseDTO;
import com.swd.notification_service.services.BroadcastNotificationService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/broadcast-notifications")
@CrossOrigin("**")
@RequiredArgsConstructor
public class BroadcastNotificationController {
    @NonNull
    private BroadcastNotificationService broadcastNotificationService;

    @PostMapping
    public ResponseEntity<BroadcastNotificationResponseDTO> createBroadcastNotification(@Valid @RequestBody CreateBroadcastNotificationRequestDTO requestDTO) {
        return ResponseEntity.ok().body(
                broadcastNotificationService.createBroadcastNotification(requestDTO)
        );
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDTO<BroadcastNotificationResponseDTO>> getBroadcastNotifications(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "15") Integer pageSize,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) Boolean executed,
            @RequestParam(required = false) Boolean deleted,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to
    ) {
        GetBroadcastNotificationListRequestDTO requestDTO = GetBroadcastNotificationListRequestDTO.builder()
                .search(search)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .order(order)
                .startDate(from)
                .endDate(to)
                .isExecuted(executed)
                .isDeleted(deleted)
                .build();
        return ResponseEntity.ok().body(
                broadcastNotificationService.getBroadcastNotifications(requestDTO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BroadcastNotificationResponseDTO> getBroadcastNotificationById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(
                broadcastNotificationService.getBroadcastNotificationById(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBroadcastNotification(@PathVariable Integer id) {
        broadcastNotificationService.deleteBroadcastNotification(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> restoreBroadcastNotification(@PathVariable Integer id) {
        broadcastNotificationService.restoreBroadcastNotification(id);
        return ResponseEntity.noContent().build();
    }
}
