package com.example.myflower.service.impl;

import com.example.myflower.consts.Constants;
import com.example.myflower.dto.auth.requests.CreateFlowerListingRequestDTO;
import com.example.myflower.dto.auth.requests.UpdateFlowerListingRequestDTO;
import com.example.myflower.dto.auth.responses.AccountResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingListResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.FlowerListing;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
import com.example.myflower.exception.flowers.FlowerListingException;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.repository.FlowerListingRepository;
import com.example.myflower.service.FlowerListingService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FlowerListingServiceImpl implements FlowerListingService {
    @NonNull
    private FlowerListingRepository flowerListingRepository;

    public FlowerListingListResponseDTO getFlowerListings(String searchString, Integer pageNumber, Integer pageSize, String sortBy, String order) {
        //Construct sort by field parameters
        Sort sort;
        switch (sortBy) {
            case Constants.SORT_FLOWER_LISTING_BY_NAME:
                sort = Sort.by(Constants.SORT_FLOWER_LISTING_BY_NAME);
                break;
            case Constants.SORT_FLOWER_LISTING_BY_PRICE:
                sort = Sort.by(Constants.SORT_FLOWER_LISTING_BY_PRICE);
                break;
            default:
                sort = Sort.by(Constants.SORT_FLOWER_LISTING_BY_CREATE_DATE);
                break;
        }
        //Construct sort order parameters
        if (Constants.SORT_ORDER_DESCENDING.equals(order)) {
            sort = sort.descending();
        }
        //Construct pagination and sort parameters
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        //Get from database
        Page<FlowerListing> flowerListingsPage;
        if (searchString != null) {
             flowerListingsPage = flowerListingRepository.findByNameContainingAndIsDeletedFalse(searchString, pageable);
        }
        else {
            flowerListingsPage = flowerListingRepository.findByIsDeletedFalse(pageable);
        }
        return this.toFlowerListingListResponseDTO(flowerListingsPage);
    }

    public FlowerListingResponseDTO getFlowerListingByID(Integer id) {
        FlowerListing result = flowerListingRepository
                .findById(id)
                .orElseThrow(() -> new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND));
        return this.toFlowerListingResponseDTO(result);
    }

    public FlowerListingResponseDTO createFlowerListing(CreateFlowerListingRequestDTO flowerListingRequestDTO, Account account) {
        FlowerListing flowerListing = FlowerListing
                .builder()
                .name(flowerListingRequestDTO.getName())
                .description(flowerListingRequestDTO.getDescription())
                .user(account)
                .address(flowerListingRequestDTO.getAddress())
                .price(flowerListingRequestDTO.getPrice())
                .stockBalance(flowerListingRequestDTO.getStockBalance())
                .createdAt(LocalDateTime.now())
                .status(FlowerListingStatusEnum.PENDING)
                .isDeleted(Boolean.FALSE)
                .build();
        FlowerListing result = flowerListingRepository.save(flowerListing);
        return this.toFlowerListingResponseDTO(result);
    }

    public FlowerListingResponseDTO updateFlowerListing(Integer id, Account account, UpdateFlowerListingRequestDTO flowerListingRequestDTO) {
        FlowerListing flowerListing = flowerListingRepository
                .findById(id)
                .orElseThrow(() -> new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND));

        //If request user is not owner of flower listing or not admin then throw error
        if (!account.getRole().equals(AccountRoleEnum.ADMIN) && !Objects.equals(flowerListing.getUser().getId(), account.getId())) {
            throw new FlowerListingException(ErrorCode.UNAUTHORIZED);
        }

        flowerListing.setName(flowerListingRequestDTO.getName());
        flowerListing.setDescription(flowerListingRequestDTO.getDescription());
        flowerListing.setAddress(flowerListingRequestDTO.getAddress());
        flowerListing.setPrice(flowerListingRequestDTO.getPrice());
        flowerListing.setStockBalance(flowerListingRequestDTO.getStockBalance());

        flowerListing.setUpdatedAt(LocalDateTime.now());
        flowerListing.setStatus(FlowerListingStatusEnum.PENDING);

        FlowerListing updatedFlowerListing = flowerListingRepository.save(flowerListing);

        return this.toFlowerListingResponseDTO(updatedFlowerListing);
    }

    private FlowerListingResponseDTO toFlowerListingResponseDTO(FlowerListing flowerListing) {
        Account account = flowerListing.getUser();
        AccountResponseDTO accountResponseDTO = AccountResponseDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .avatar(account.getAvatar())
                .phone(account.getPhone())
                .build();
        return FlowerListingResponseDTO.builder()
                .id(flowerListing.getId())
                .name(flowerListing.getName())
                .description(flowerListing.getDescription())
                .price(flowerListing.getPrice())
                .user(accountResponseDTO)
                .stockBalance(flowerListing.getStockBalance())
                .createdAt(flowerListing.getCreatedAt())
                .build();
    }

    private FlowerListingListResponseDTO toFlowerListingListResponseDTO(Page<FlowerListing> flowerListingPage) {
        List<FlowerListingResponseDTO> flowerListingResponseDTOList = flowerListingPage
                .stream()
                .map(this::toFlowerListingResponseDTO)
                .toList();
        return FlowerListingListResponseDTO.builder()
                .content(flowerListingResponseDTOList)
                .pageNumber(flowerListingPage.getNumber())
                .pageSize(flowerListingPage.getSize())
                .totalPages(flowerListingPage.getTotalPages())
                .numberOfElements(flowerListingPage.getNumberOfElements())
                .totalElements(flowerListingPage.getTotalElements())
                .build();
    }
}
