package com.example.myflower.mapper;

import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingListResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.dto.flowercategogy.response.FlowerCategoryResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.FlowerListing;
import org.springframework.data.domain.Page;

import java.util.List;

public class FlowerListingMapper {
    private FlowerListingMapper() {}

    public static FlowerListingResponseDTO toFlowerListingResponseDTO(FlowerListing flowerListing) {
        Account account = flowerListing.getUser();
        AccountResponseDTO accountResponseDTO = AccountResponseDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .avatar(account.getAvatar())
                .phone(account.getPhone())
                .build();
        //Map flower category
        List<FlowerCategoryResponseDTO> flowerCategories = flowerListing.getCategories().stream()
                .map(FlowerCategoryMapper::toCategoryResponseDTO)
                .toList();
        return FlowerListingResponseDTO.builder()
                .id(flowerListing.getId())
                .name(flowerListing.getName())
                .description(flowerListing.getDescription())
                .price(flowerListing.getPrice())
                .user(accountResponseDTO)
                .stockQuantity(flowerListing.getStockQuantity())
                .categories(flowerCategories)
                .imageUrl(flowerListing.getImageUrl())
                .status(flowerListing.getStatus())
                .createdAt(flowerListing.getCreatedAt())
                .updatedAt(flowerListing.getUpdatedAt())
                .build();
    }

    public static FlowerListingListResponseDTO toFlowerListingListResponseDTO(Page<FlowerListing> flowerListingPage) {
        List<FlowerListingResponseDTO> flowerListingResponseDTOList = flowerListingPage
                .stream()
                .map(FlowerListingMapper::toFlowerListingResponseDTO)
                .toList();
        return FlowerListingListResponseDTO.builder()
                .content(flowerListingResponseDTOList)
                .pageNumber(flowerListingPage.getNumber())
                .pageSize(flowerListingPage.getSize())
                .totalPages(flowerListingPage.getTotalPages())
                .numberOfElements(flowerListingPage.getNumberOfElements())
                .totalElements(flowerListingPage.getTotalElements())
                .build();
    }
}