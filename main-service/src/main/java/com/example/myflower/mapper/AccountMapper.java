package com.example.myflower.mapper;

import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.pagination.PaginationResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountMapper {

    private final StorageService storageService;

    @Autowired
    public AccountMapper(StorageService storageService) {
        this.storageService = storageService;
    }

    public AccountResponseDTO mapToAccountResponseDTO(Account account) {
        String avatarUrl = null;

        // Manually handle the logic that would normally be in @PostLoad
        if (account.getAvatar() != null) {
            avatarUrl = storageService.getFileUrl(account.getAvatar());
        }

        return AccountResponseDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .phone(account.getPhone())
                .gender(account.getGender())
                .role(account.getRole())
                .externalAuthType(account.getExternalAuthType())
                .avatar(avatarUrl)  // Set the avatar URL from storage service
                .balance(account.getBalance())
                .status(account.getStatus())
                .createAt(account.getCreateAt())
                .updateAt(account.getUpdateAt())
                .build();
    }

    public PaginationResponseDTO<AccountResponseDTO> toPaginationResponseDTO(Page<Account> flowerListingPage) {
        List<AccountResponseDTO> flowerListingResponseDTOList = flowerListingPage.getContent().stream()
                .map(this::mapToAccountResponseDTO)
                .toList();
        return PaginationResponseDTO.<AccountResponseDTO>builder()
                .content(flowerListingResponseDTOList)
                .pageNumber(flowerListingPage.getNumber())
                .pageSize(flowerListingPage.getSize())
                .totalPages(flowerListingPage.getTotalPages())
                .numberOfElements(flowerListingPage.getNumberOfElements())
                .totalElements(flowerListingPage.getTotalElements())
                .build();
    }
}
