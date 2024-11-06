package com.example.myflower.controller;

import com.example.myflower.dto.auth.requests.CreateFlowerListingRequestDTO;
import com.example.myflower.dto.auth.requests.GetFlowerListingsRequestDTO;
import com.example.myflower.dto.auth.requests.UpdateFlowerListingRequestDTO;
import com.example.myflower.dto.pagination.PaginationResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
import com.example.myflower.service.FlowerListingService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/flowers")
@CrossOrigin("**")
@RequiredArgsConstructor
public class FlowerListingController {
    @NonNull
    private FlowerListingService flowerListingService;

    @GetMapping
    public ResponseEntity<PaginationResponseDTO<FlowerListingResponseDTO>> getFlowerListings(
            @RequestParam(required = false, defaultValue = "") String searchString,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false) FlowerListingStatusEnum status,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean deleted,
            @RequestParam(required = false) List<Integer> categoryIds
    )
    {
        GetFlowerListingsRequestDTO requestDTO = GetFlowerListingsRequestDTO.builder()
                .searchString(searchString)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .flowerStatus(status)
                .order(order)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .deleteStatus(deleted)
                .categoryIds(categoryIds)
                .build();
        return ResponseEntity.ok().body(flowerListingService.getFlowerListings(requestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlowerListingResponseDTO> getFlowerListingById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(flowerListingService.getFlowerListingByID(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FlowerListingResponseDTO> createFlowerListing(
            @AuthenticationPrincipal Account account,
            @Valid @ModelAttribute CreateFlowerListingRequestDTO flowerListingRequestDTO
    ) {
        return ResponseEntity.ok().body(flowerListingService.createFlowerListing(flowerListingRequestDTO, account));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FlowerListingResponseDTO> updateFlowerListingById(
            @PathVariable Integer id,
            @AuthenticationPrincipal Account account,
            @Valid @ModelAttribute UpdateFlowerListingRequestDTO flowerListingRequestDTO
    ) {
        return ResponseEntity.ok().body(flowerListingService.updateFlowerListing(id, account, flowerListingRequestDTO));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FlowerListingResponseDTO>> getFlowerListingByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok().body(flowerListingService.getFlowerListingsByUserID(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlowerListingById(@PathVariable Integer id) {
        flowerListingService.deleteFlower(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restoreFlowerListingById(@PathVariable Integer id) {
        flowerListingService.restoreFlower(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PostMapping("/clear-cache")
    public ResponseEntity<Void> clearFlowerListingCache() {
        flowerListingService.clearFlowerListingCache();
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PostMapping("/disable-expired-flowers")
    public ResponseEntity<Void> disableExpiredFlowers() {
        flowerListingService.disableExpiredFlowers();
        return ResponseEntity.noContent().build();
    }
}
