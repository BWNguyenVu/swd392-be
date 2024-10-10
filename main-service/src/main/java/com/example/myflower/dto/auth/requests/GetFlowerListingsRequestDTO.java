package com.example.myflower.dto.auth.requests;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetFlowerListingsRequestDTO {
    private String searchString;
    private Integer pageNumber;
    private Integer pageSize;
    private String sortBy;
    private String order;
    private List<Integer> categoryIds;
}