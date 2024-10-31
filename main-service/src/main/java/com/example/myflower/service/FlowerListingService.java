package com.example.myflower.service;

import com.example.myflower.dto.auth.requests.CreateFlowerListingRequestDTO;
import com.example.myflower.dto.auth.requests.GetFlowerListingsRequestDTO;
import com.example.myflower.dto.auth.requests.UpdateFlowerListingRequestDTO;
import com.example.myflower.dto.pagination.PaginationResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.dto.file.FileResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.FlowerListing;

import java.util.List;

public interface FlowerListingService {
    PaginationResponseDTO<FlowerListingResponseDTO> getFlowerListings(GetFlowerListingsRequestDTO requestDTO);
    FlowerListingResponseDTO getFlowerListingByID(Integer id);

    List<FlowerListingResponseDTO> getFlowerListingsByUserID(Integer userId);

    FlowerListingResponseDTO createFlowerListing(CreateFlowerListingRequestDTO flowerListingRequestDTO, Account account);
    FlowerListingResponseDTO updateFlowerListing(Integer id, Account account, UpdateFlowerListingRequestDTO flowerListingRequestDTO);
    void clearFlowerListingCache();

    void deleteFlower(Integer id);

    void restoreFlower(Integer id);

    Integer countProductBySeller(Integer sellerId);
    void updateViewsFlowerListing(Integer flowerListingId, Integer views);

    void updateQuantityFlowerListing(FlowerListing flowerListing, Integer quantity);

    List<FileResponseDTO> getFlowerImages(Integer flowerId);

    FileResponseDTO getFeaturedFlowerImage(Integer flowerId);

    FlowerListingResponseDTO approveFlowerListing(Integer id);

    FlowerListingResponseDTO rejectFlowerListing(Integer id, String reason);

    void disableExpiredFlowers();
}
