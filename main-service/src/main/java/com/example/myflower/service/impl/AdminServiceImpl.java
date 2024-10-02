package com.example.myflower.service.impl;

import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.FlowerListing;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.flowers.FlowerListingException;
import com.example.myflower.mapper.FlowerListingMapper;
import com.example.myflower.repository.AccountRepository;
import com.example.myflower.repository.FlowerListingRepository;
import com.example.myflower.service.AdminService;
import com.example.myflower.utils.AccountUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    @NonNull
    private final FlowerListingRepository flowerListingRepository;
    private final AccountRepository accountRepository;

    @Override
    public FlowerListingResponseDTO approveFlowerListing(Integer id) {
        Account adminAccount = AccountUtils.getCurrentAccount();

        if (!(adminAccount != null && AccountRoleEnum.ADMIN.equals(adminAccount.getRole()))) {
            throw new FlowerListingException(ErrorCode.UNAUTHORIZED);
        }

        FlowerListing flowerListing = flowerListingRepository.findByIdAndDeleteStatus(id, Boolean.FALSE)
                .orElseThrow(() -> new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND));

        flowerListing.setStatus(FlowerListingStatusEnum.APPROVED);
        flowerListing.setUpdatedAt(LocalDateTime.now());

        FlowerListing approvedListing = flowerListingRepository.save(flowerListing);
        return FlowerListingMapper.toFlowerListingResponseDTO(approvedListing);
    }

    @Override
    public FlowerListingResponseDTO rejectFlowerListing(Integer id, String reason) {
        Account adminAccount = AccountUtils.getCurrentAccount();

        //Check if the requester is admin
        if (!(adminAccount != null && AccountRoleEnum.ADMIN.equals(adminAccount.getRole()))) {
            throw new FlowerListingException(ErrorCode.UNAUTHORIZED);
        }

        FlowerListing flowerListing = flowerListingRepository.findByIdAndDeleteStatus(id, Boolean.FALSE)
                .orElseThrow(() -> new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND));

        flowerListing.setStatus(FlowerListingStatusEnum.REJECTED);
        flowerListing.setRejectReason(reason);
        flowerListing.setUpdatedAt(LocalDateTime.now());

        FlowerListing rejectedListing = flowerListingRepository.save(flowerListing);
        return FlowerListingMapper.toFlowerListingResponseDTO(rejectedListing);
    }

    @Override
    public Account getAccountAdmin (){
        return accountRepository.findAccountsByRole(AccountRoleEnum.ADMIN);
    }
}
