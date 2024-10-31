package com.swd.notification_service.dto.pagination;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorPaginationRequest<T> {
    private Integer size;
    private T cursor;
}
