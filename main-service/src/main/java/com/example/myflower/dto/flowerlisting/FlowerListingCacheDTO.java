package com.example.myflower.dto.flowerlisting;

import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowerListingCacheDTO {
    private Integer id;
    private Integer userId;
    private String name;
    private String description;
    private BigDecimal price;
    private String eventType;
    private Integer stockQuantity;
    private String address;
    private List<Integer> categories;
    private List<Integer> images;
    private FlowerListingStatusEnum status;
    private LocalDateTime expireDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer views;
    private Boolean isDeleted;
    private String rejectReason;
}
