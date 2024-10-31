package com.swd.notification_service.dto.flowers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swd.notification_service.dto.flowers.EnumType.CategoryParentEnum;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlowerCategoryResponseDTO implements Serializable {
    private Integer id;
    private String name;
    private CategoryParentEnum categoryParent;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
