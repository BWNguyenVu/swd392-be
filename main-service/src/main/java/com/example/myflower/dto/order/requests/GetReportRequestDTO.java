package com.example.myflower.dto.order.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
public class GetReportRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;
}
