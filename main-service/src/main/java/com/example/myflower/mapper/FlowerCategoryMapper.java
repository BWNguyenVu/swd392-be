package com.example.myflower.mapper;

import com.example.myflower.dto.flowercategogy.response.FlowerCategoryResponseDTO;
import com.example.myflower.entity.FlowerCategory;
import com.example.myflower.service.StorageService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlowerCategoryMapper {
    @NonNull
    private StorageService storageService;

    public FlowerCategoryResponseDTO toCategoryResponseDTO(FlowerCategory flowerCategory) {
        String imageUrl = storageService.getFileUrl(flowerCategory.getImageUrl());
        return FlowerCategoryResponseDTO.builder()
                .id(flowerCategory.getId())
                .name(flowerCategory.getName())
                .categoryParent(flowerCategory.getCategoryParent())
                .imageUrl(imageUrl)
                .createdAt(flowerCategory.getCreatedAt())
                .updatedAt(flowerCategory.getUpdatedAt())
                .isDeleted(flowerCategory.isDeleted())
                .build();
    }
}
