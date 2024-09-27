package com.example.myflower.controller;

import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private IAdminService adminService;

    @PutMapping("/flower-listings/approve/{id}")
    public FlowerListingResponseDTO approveListing(@PathVariable Integer id) {
        return adminService.approveFlowerListing(id);
    }

    @PutMapping("/flower-listings/reject/{id}")
    public FlowerListingResponseDTO rejectListing(@PathVariable Integer id, @RequestParam String reason) {
        return adminService.rejectFlowerListing(id, reason);
    }
}
