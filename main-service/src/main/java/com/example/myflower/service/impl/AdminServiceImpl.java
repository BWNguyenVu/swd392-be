package com.example.myflower.service.impl;

import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.FlowerListing;
import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.flowers.FlowerListingException;
import com.example.myflower.repository.FlowerListingRepository;
import com.example.myflower.service.IAdminService;
import com.example.myflower.utils.AccountUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements IAdminService {

    @NonNull
    private final FlowerListingRepository flowerListingRepository;

    @Override
    public FlowerListingResponseDTO approveFlowerListing(Integer id) {
        Account adminAccount = AccountUtils.getCurrentAccount();

        FlowerListing flowerListing = flowerListingRepository.findById(id)
                .orElseThrow(() -> new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND));

        flowerListing.setStatus(FlowerListingStatusEnum.APPROVED);
        flowerListing.setUpdatedAt(LocalDateTime.now());

        FlowerListing approvedListing = flowerListingRepository.save(flowerListing);
        return this.toFlowerListingResponseDTO(approvedListing);
    }

    @Override
    public FlowerListingResponseDTO rejectFlowerListing(Integer id, String reason) {
        Account adminAccount = AccountUtils.getCurrentAccount();

        FlowerListing flowerListing = flowerListingRepository.findById(id)
                .orElseThrow(() -> new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND));

        flowerListing.setStatus(FlowerListingStatusEnum.REJECTED);
        flowerListing.setRejectReason(reason);
        flowerListing.setUpdatedAt(LocalDateTime.now());

        FlowerListing rejectedListing = flowerListingRepository.save(flowerListing);
        return this.toFlowerListingResponseDTO(rejectedListing);
    }

    // Helper method to convert entity to DTO
    private FlowerListingResponseDTO toFlowerListingResponseDTO(FlowerListing flowerListing) {
        return FlowerListingResponseDTO.builder()
                .id(flowerListing.getId())
                .name(flowerListing.getName())
                .description(flowerListing.getDescription())
                .price(flowerListing.getPrice())
                .status(FlowerListingStatusEnum.valueOf(flowerListing.getStatus().name()))
                .rejectReason(flowerListing.getRejectReason())
                .createdAt(flowerListing.getCreatedAt())
                .updatedAt(flowerListing.getUpdatedAt())
                .build();
    }
}
