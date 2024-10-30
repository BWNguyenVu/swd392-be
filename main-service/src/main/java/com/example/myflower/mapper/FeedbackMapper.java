package com.example.myflower.mapper;

import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.dto.feedback.response.FeedbackResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.Feedback;
import com.example.myflower.entity.FlowerListing;

public class FeedbackMapper {
    private FeedbackMapper() {}

    public static FeedbackResponseDTO toResponseDTOWithFlowerData(Feedback feedback) {
        Account account = feedback.getUser();
        AccountResponseDTO accountResponseDTO = AccountResponseDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .avatar(account.getAvatar())
                .phone(account.getPhone())
                .build();
        FlowerListing flowerListing = feedback.getFlower();
        FlowerListingResponseDTO flowerListingResponseDTO = null;
        if (flowerListing != null) {
            flowerListingResponseDTO = FlowerListingResponseDTO.builder()
                    .id(flowerListing.getId())
                    .name(flowerListing.getName())
                    .build();
        }
        return FeedbackResponseDTO.builder()
                .id(feedback.getId())
                .user(accountResponseDTO)
                .flowerListing(flowerListingResponseDTO)
                .description(feedback.getDescription())
                .rating(feedback.getRating().getValue())
                .createdAt(feedback.getCreatedAt())
                .build();
    }

    public static FeedbackResponseDTO toResponseDTO(Feedback feedback) {
        Account account = feedback.getUser();
        AccountResponseDTO accountResponseDTO = AccountResponseDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .avatar(account.getAvatar())
                .phone(account.getPhone())
                .build();
        return FeedbackResponseDTO.builder()
                .id(feedback.getId())
                .user(accountResponseDTO)
                .description(feedback.getDescription())
                .rating(feedback.getRating().getValue())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}
