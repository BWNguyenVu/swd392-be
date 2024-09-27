package com.example.myflower.service;

import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;

public interface IAdminService {
    FlowerListingResponseDTO approveFlowerListing(Integer id);
    FlowerListingResponseDTO rejectFlowerListing(Integer id, String reason);
}

