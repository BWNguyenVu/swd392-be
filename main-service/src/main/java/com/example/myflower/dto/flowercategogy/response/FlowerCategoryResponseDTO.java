package com.example.myflower.dto.flowercategogy.response;

import com.example.myflower.entity.enumType.CategoryParentEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
