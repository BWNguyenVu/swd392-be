package com.example.myflower.dto.auth.requests;

import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
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
    private Boolean deleteStatus;
    private FlowerListingStatusEnum flowerStatus;
    private List<Integer> categoryIds;
}