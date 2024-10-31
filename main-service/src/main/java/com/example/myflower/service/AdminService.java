package com.example.myflower.service;

import com.example.myflower.dto.admin.requests.CreateAccountIntegrationRequest;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.entity.Account;

public interface AdminService {
    Account getAccountAdmin();
    void createAccountIntegration(CreateAccountIntegrationRequest requestDTO);
}

