package com.example.myflower.mapper;

import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.dto.feedback.response.FeedbackResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.Feedback;
import com.example.myflower.entity.FlowerListing;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackMapper {
    @NonNull
    private AccountMapper accountMapper;

    public FeedbackResponseDTO toResponseDTOWithFlowerData(Feedback feedback) {
        Account account = feedback.getUser();
        AccountResponseDTO accountResponseDTO = accountMapper.mapToAccountResponseDTO(account);
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

    public FeedbackResponseDTO toResponseDTO(Feedback feedback) {
        Account account = feedback.getUser();
        AccountResponseDTO accountResponseDTO = accountMapper.mapToAccountResponseDTO(account);
        return FeedbackResponseDTO.builder()
                .id(feedback.getId())
                .user(accountResponseDTO)
                .description(feedback.getDescription())
                .rating(feedback.getRating().getValue())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}
