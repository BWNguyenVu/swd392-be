package com.swd.notification_service.mapper;

import com.swd.notification_service.dto.broadcast_notification.response.BroadcastNotificationResponseDTO;
import com.swd.notification_service.dto.pagination.response.PaginationResponseDTO;
import com.swd.notification_service.entity.BroadcastNotification;
import org.springframework.data.domain.Page;

import java.util.List;

public class BroadcastNotificationMapper {
    private BroadcastNotificationMapper() {}
    public static BroadcastNotificationResponseDTO toResponseDTO(final BroadcastNotification entity) {
        if (entity == null) {
            return null;
        }
        return BroadcastNotificationResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .type(entity.getType())
                .executeTime(entity.getExecuteTime())
                .isExecuted(entity.getIsExecuted())
                .scheduledTime(entity.getScheduledTime())
                .destinationScreen(entity.getDestinationScreen())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .message(entity.getMessage())
                .build();
    }

    public static PaginationResponseDTO<BroadcastNotificationResponseDTO> toPaginationResponseDTO(final Page<BroadcastNotification> page) {
        List<BroadcastNotificationResponseDTO> responseDTOs = page.stream()
                .map(BroadcastNotificationMapper::toResponseDTO)
                .toList();
        return PaginationResponseDTO.<BroadcastNotificationResponseDTO>builder()
                .content(responseDTOs)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .build();
    }
}
