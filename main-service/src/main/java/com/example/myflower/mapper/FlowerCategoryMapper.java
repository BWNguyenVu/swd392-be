package com.example.myflower.mapper;

import com.example.myflower.dto.flowercategogy.response.FlowerCategoryResponseDTO;
import com.example.myflower.entity.FlowerCategory;

public class FlowerCategoryMapper {
    private FlowerCategoryMapper() {}

    public static FlowerCategoryResponseDTO toCategoryResponseDTO(FlowerCategory flowerCategory) {
        return FlowerCategoryResponseDTO.builder()
                .id(flowerCategory.getId())
                .name(flowerCategory.getName())
                .categoryParent(flowerCategory.getCategoryParent())
                .createdAt(flowerCategory.getCreatedAt())
                .updatedAt(flowerCategory.getUpdatedAt())
                .build();
    }
}
