package com.example.myflower.service;

import com.example.myflower.dto.auth.requests.CreateFlowerListingRequestDTO;
import com.example.myflower.dto.auth.requests.UpdateFlowerListingRequestDTO;
import com.example.myflower.dto.auth.responses.FlowerListingListResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.entity.Account;

public interface FlowerListingService {
    FlowerListingListResponseDTO getFlowerListings(String searchString, Integer pageNumber, String sortBy, String order);
    FlowerListingResponseDTO getFlowerListingByID(Integer id);
    FlowerListingResponseDTO createFlowerListing(CreateFlowerListingRequestDTO flowerListingRequestDTO, Account account);
    FlowerListingResponseDTO updateFlowerListing(Integer id, Account account, UpdateFlowerListingRequestDTO flowerListingRequestDTO);
}
