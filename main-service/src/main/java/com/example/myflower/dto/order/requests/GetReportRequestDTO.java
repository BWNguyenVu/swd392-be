package com.example.myflower.dto.order.requests;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class GetReportRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;
}
