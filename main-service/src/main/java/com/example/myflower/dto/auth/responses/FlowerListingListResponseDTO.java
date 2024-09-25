package com.example.myflower.dto.auth.responses;

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
public class FlowerListingListResponseDTO implements Serializable {
    private List<FlowerListingResponseDTO> content;
    private Integer pageNumber;
    private Integer totalPages;
    private Integer pageSize;
    private Integer numberOfElements;
    private Long totalElements;
}
