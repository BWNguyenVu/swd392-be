package com.example.myflower.dto.pagination;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponseDTO<T extends Serializable> implements Serializable {
    private List<T> content;
    private Integer pageNumber;
    private Integer totalPages;
    private Integer pageSize;
    private Integer numberOfElements;
    private Long totalElements;
}