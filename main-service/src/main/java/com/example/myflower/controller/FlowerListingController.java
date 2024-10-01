package com.example.myflower.controller;

import com.example.myflower.dto.auth.requests.CreateFlowerListingRequestDTO;
import com.example.myflower.dto.auth.requests.GetFlowerListingsRequestDTO;
import com.example.myflower.dto.auth.requests.UpdateFlowerListingRequestDTO;
import com.example.myflower.dto.auth.responses.FlowerListingListResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.service.FlowerListingService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flowers")
@CrossOrigin("*")
@RequiredArgsConstructor
public class FlowerListingController {
    @NonNull
    private FlowerListingService flowerListingService;

    @GetMapping
    public ResponseEntity<FlowerListingListResponseDTO> getFlowerListings(
            @RequestParam(required = false, defaultValue = "") String searchString,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) List<Integer> categoryIds)
    {
        GetFlowerListingsRequestDTO requestDTO = GetFlowerListingsRequestDTO.builder()
                .searchString(searchString)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .order(order)
                .categoryIds(categoryIds)
                .build();
        return ResponseEntity.ok().body(flowerListingService.getFlowerListings(requestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlowerListingResponseDTO> getFlowerListingById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(flowerListingService.getFlowerListingByID(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FlowerListingResponseDTO> createFlowerListing(@AuthenticationPrincipal Account account, @Valid @ModelAttribute CreateFlowerListingRequestDTO flowerListingRequestDTO) {
        return ResponseEntity.ok().body(flowerListingService.createFlowerListing(flowerListingRequestDTO, account));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlowerListingResponseDTO> updateFlowerListingByID(
            @PathVariable Integer id,
            @AuthenticationPrincipal Account account,
            @RequestBody UpdateFlowerListingRequestDTO flowerListingRequestDTO
    ) {
        return ResponseEntity.ok().body(flowerListingService.updateFlowerListing(id, account, flowerListingRequestDTO));
    }
}
