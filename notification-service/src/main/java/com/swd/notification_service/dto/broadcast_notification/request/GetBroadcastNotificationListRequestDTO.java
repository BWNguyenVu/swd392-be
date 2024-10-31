package com.swd.notification_service.dto.broadcast_notification.request;

import com.swd.notification_service.dto.pagination.request.PaginationRequestDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Getter
@Setter
@RequiredArgsConstructor
public class GetBroadcastNotificationListRequestDTO extends PaginationRequestDTO {
    private String search;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isExecuted;
    private Boolean isDeleted;
}
