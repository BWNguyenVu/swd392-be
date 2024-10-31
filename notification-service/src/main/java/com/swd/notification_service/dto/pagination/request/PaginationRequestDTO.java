package com.swd.notification_service.dto.pagination.request;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequestDTO {
    private Integer pageNumber;
    private Integer pageSize;
    private String sortBy;
    private String order;
}
