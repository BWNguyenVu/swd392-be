package com.example.myflower.controller;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.account.requests.AddBalanceRequestDTO;
import com.example.myflower.dto.account.requests.GetUsersRequestDTO;
import com.example.myflower.dto.account.requests.UpdateAccountRequestDTO;
import com.example.myflower.dto.account.requests.UploadFileRequestDTO;
import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.account.responses.AddBalanceResponseDTO;
import com.example.myflower.dto.account.responses.GetBalanceResponseDTO;
import com.example.myflower.dto.account.responses.SellerResponseDTO;
import com.example.myflower.dto.pagination.PaginationResponseDTO;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import com.example.myflower.entity.enumType.AccountStatusEnum;
import com.example.myflower.exception.account.AccountAppException;
import com.example.myflower.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/account")
@CrossOrigin("**")
public class    AccountController {
    @Autowired
    private AccountService accountService;

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @PostMapping("/add-balance")
    public ResponseEntity<AddBalanceResponseDTO> addBalance(@Valid @RequestBody AddBalanceRequestDTO addBalanceRequestDTO) {
        return accountService.addBalance(addBalanceRequestDTO);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/balance")
    public ResponseEntity<GetBalanceResponseDTO> getBalance() {
        return accountService.getBalance();
    }

    @GetMapping("/profile")
    public ResponseEntity<BaseResponseDTO> getProfile() {
        try {
            final String message = "Get profile successful";
            AccountResponseDTO accountResponse = accountService.getProfile();
            return ResponseEntity.status(HttpStatus.OK).body(
                    BaseResponseDTO.builder()
                            .message(message)
                            .data(accountResponse)
                            .build()
            );
        } catch (AccountAppException e) {
            BaseResponseDTO errorResponse = BaseResponseDTO.builder()
                    .message(e.getErrorCode().getMessage())
                    .build();
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(errorResponse);
        }
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @PostMapping(value = "/upload-avatar", consumes = "multipart/form-data")
    public ResponseEntity<BaseResponseDTO> uploadAvatar(@ModelAttribute UploadFileRequestDTO uploadFileRequestDTO) throws IOException {
        AccountResponseDTO accountResponseDTO = accountService.uploadAvatar(uploadFileRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDTO.builder()
                        .message("Upload avatar successful")
                        .success(true)
                        .data(accountResponseDTO)
                        .build()
        );
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @PatchMapping("/update-profile")
    public ResponseEntity<BaseResponseDTO> updateProfile(@RequestBody UpdateAccountRequestDTO updateAccountRequestDTO){
        AccountResponseDTO accountResponseDTO = accountService.updateProfile(updateAccountRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDTO.builder()
                        .message("Update profile successful")
                        .success(true)
                        .data(accountResponseDTO)
                        .build());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/update-status-user")
    public ResponseEntity<BaseResponseDTO> updateStatusUser(@RequestBody AccountStatusEnum status){
        UpdateAccountRequestDTO updateAccountRequestDTO = UpdateAccountRequestDTO.builder()
                .status(status)
                .build();
        AccountResponseDTO accountResponseDTO = accountService.updateStatusUser(updateAccountRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDTO.builder()
                .message("Update profile successful")
                .success(true)
                .data(accountResponseDTO)
                .build());
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<BaseResponseDTO> getSellerById(@PathVariable("profileId") Integer profileId) {
        try {
            final String message = "Get profile successful";
            SellerResponseDTO accountResponse = accountService.getSellerById(profileId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    BaseResponseDTO.builder()
                            .message(message)
                            .data(accountResponse)
                            .build()
            );
        } catch (AccountAppException e) {
            BaseResponseDTO errorResponse = BaseResponseDTO.builder()
                    .message(e.getErrorCode().getMessage())
                    .build();
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(errorResponse);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/profile/all")
    public ResponseEntity<BaseResponseDTO> getAllUser(@RequestParam(required = false, defaultValue = "") String search,
                                                      @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                                      @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                                                      @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                      @RequestParam(required = false) String order,
                                                      @RequestParam(required = false) List<AccountRoleEnum> roles
    ) {
        try {
            GetUsersRequestDTO requestDTO = GetUsersRequestDTO.builder()
                    .search(search)
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .sortBy(sortBy)
                    .order(order)
                    .roles(roles)
                    .build();
            PaginationResponseDTO<AccountResponseDTO> accountResponseDTO = accountService.getAllUser(requestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(
                    BaseResponseDTO.builder()
                            .message("Get all profile successful")
                            .success(true)
                            .data(accountResponseDTO)
                            .build()
            );
        } catch (AccountAppException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(
                    BaseResponseDTO.builder()
                            .message(e.getErrorCode().getMessage())
                            .success(false)
                            .code(e.getErrorCode().getCode())
                            .build()
            );
        }
    }
}
