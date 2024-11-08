package com.example.myflower.mapper;

import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.file.FileResponseDTO;
import com.example.myflower.dto.flowerlisting.FlowerListingCacheDTO;
import com.example.myflower.dto.pagination.PaginationResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.dto.flowercategogy.response.FlowerCategoryResponseDTO;
import com.example.myflower.entity.*;
import com.example.myflower.service.StorageService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FlowerListingMapper {
    @NonNull
    private AccountMapper accountMapper;
    @NonNull
    private MediaFileMapper mediaFileMapper;
    @NonNull
    private FlowerCategoryMapper flowerCategoryMapper;

    public FlowerListingResponseDTO toFlowerListingResponseDTO(FlowerListing flowerListing) {
        Account account = flowerListing.getUser();
        AccountResponseDTO accountResponseDTO = accountMapper.mapToAccountResponseDTO(account);
        List<FileResponseDTO> images = flowerListing.getImages().stream()
                .map(FlowerImage::getMediaFile)
                .map(mediaFileMapper::toResponseDTOWithUrl).toList();
        //Map flower category
        List<FlowerCategoryResponseDTO> flowerCategories = flowerListing.getCategories().stream()
                .map(flowerCategoryMapper::toCategoryResponseDTO)
                .toList();
        return FlowerListingResponseDTO.builder()
                .id(flowerListing.getId())
                .name(flowerListing.getName())
                .description(flowerListing.getDescription())
                .price(flowerListing.getPrice())
                .user(accountResponseDTO)
                .stockQuantity(flowerListing.getStockQuantity())
                .address(flowerListing.getAddress())
                .categories(flowerCategories)
                .images(images)
                .status(flowerListing.getStatus())
                .expireDate(flowerListing.getExpireDate())
                .flowerExpireDate(flowerListing.getFlowerExpireDate())
                .views(flowerListing.getViews())
                .createdAt(flowerListing.getCreatedAt())
                .updatedAt(flowerListing.getUpdatedAt())
                .isDeleted(flowerListing.isDeleted())
                .build();
    }

    public FlowerListingCacheDTO toCacheDTO(FlowerListing flowerListing) {
        return FlowerListingCacheDTO.builder()
                .id(flowerListing.getId())
                .name(flowerListing.getName())
                .description(flowerListing.getDescription())
                .price(flowerListing.getPrice())
                .userId(flowerListing.getUser().getId())
                .stockQuantity(flowerListing.getStockQuantity())
                .expireDate(flowerListing.getExpireDate())
                .flowerExpireDate(flowerListing.getFlowerExpireDate())
                .address(flowerListing.getAddress())
                .categories(
                        flowerListing.getCategories().stream()
                                .map(FlowerCategory::getId)
                                .toList()
                )
                .images(
                        flowerListing.getImages().stream()
                                .map(FlowerImage::getMediaFile)
                                .map(MediaFile::getId)
                                .toList()
                )
                .status(flowerListing.getStatus())
                .views(flowerListing.getViews())
                .createdAt(flowerListing.getCreatedAt())
                .updatedAt(flowerListing.getUpdatedAt())
                .isDeleted(flowerListing.isDeleted())
                .build();
    }

    public FlowerListingResponseDTO toResponseDTO(
            FlowerListingCacheDTO cacheDTO,
            AccountResponseDTO accountResponseDTO,
            List<FlowerCategoryResponseDTO> flowerCategoryResponseDTO,
            List<FileResponseDTO> fileResponseDTO
    ) {
        return FlowerListingResponseDTO.builder()
                .id(cacheDTO.getId())
                .name(cacheDTO.getName())
                .description(cacheDTO.getDescription())
                .price(cacheDTO.getPrice())
                .user(accountResponseDTO)
                .stockQuantity(cacheDTO.getStockQuantity())
                .address(cacheDTO.getAddress())
                .categories(flowerCategoryResponseDTO)
                .images(fileResponseDTO)
                .status(cacheDTO.getStatus())
                .views(cacheDTO.getViews())
                .expireDate(cacheDTO.getExpireDate())
                .flowerExpireDate(cacheDTO.getFlowerExpireDate())
                .createdAt(cacheDTO.getCreatedAt())
                .updatedAt(cacheDTO.getUpdatedAt())
                .isDeleted(cacheDTO.getIsDeleted())
                .build();
    }

    public PaginationResponseDTO<FlowerListingResponseDTO> toFlowerListingListResponseDTO(Page<FlowerListing> flowerListingPage) {
        List<FlowerListingResponseDTO> flowerListingResponseDTOList = flowerListingPage
                .stream()
                .map(this::toFlowerListingResponseDTO)
                .toList();
        return PaginationResponseDTO.<FlowerListingResponseDTO>builder()
                .content(flowerListingResponseDTOList)
                .pageNumber(flowerListingPage.getNumber())
                .pageSize(flowerListingPage.getSize())
                .totalPages(flowerListingPage.getTotalPages())
                .numberOfElements(flowerListingPage.getNumberOfElements())
                .totalElements(flowerListingPage.getTotalElements())
                .build();
    }
}