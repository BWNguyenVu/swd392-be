package com.example.myflower.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasePaginationRequestDTO {
    private Integer pageNumber;
    private Integer pageSize;
    private String sortBy;
    private String order;
    private LocalDate startDate;
    private LocalDate endDate;
}
