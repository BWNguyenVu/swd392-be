package com.example.myflower.controller;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.admin.requests.CreateAccountIntegrationRequest;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin("**")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/flower-listings/approve/{id}")
    public FlowerListingResponseDTO approveListing(@PathVariable Integer id) {
        return adminService.approveFlowerListing(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/flower-listings/reject/{id}")
    public FlowerListingResponseDTO rejectListing(@PathVariable Integer id, @RequestParam String reason) {
        return adminService.rejectFlowerListing(id, reason);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create-account-integration")
    public ResponseEntity<BaseResponseDTO> createAccountIntegration(@Valid @RequestBody CreateAccountIntegrationRequest requestDTO) {
        adminService.createAccountIntegration(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponseDTO.builder()
                        .message("Create account integration successfully")
                        .success(true)
                        .build()
        );
    }
}
