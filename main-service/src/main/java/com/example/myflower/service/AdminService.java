package com.example.myflower.service;

import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.entity.Account;

public interface AdminService {
    FlowerListingResponseDTO approveFlowerListing(Integer id);
    FlowerListingResponseDTO rejectFlowerListing(Integer id, String reason);
    Account getAccountAdmin();
}

