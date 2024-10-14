package com.example.myflower.controller;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.account.requests.AddBalanceRequestDTO;
import com.example.myflower.dto.account.requests.UpdateAccountRequestDTO;
import com.example.myflower.dto.account.requests.UploadFileRequestDTO;
import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.account.responses.AddBalanceResponseDTO;
import com.example.myflower.dto.account.responses.GetBalanceResponseDTO;
import com.example.myflower.exception.account.AccountAppException;
import com.example.myflower.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/account")
@CrossOrigin("**")
public class AccountController {
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

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
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
}
