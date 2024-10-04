package com.example.myflower.controller;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.account.requests.AddBalanceRequestDTO;
import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.account.responses.AddBalanceResponseDTO;
import com.example.myflower.dto.account.responses.GetBalanceResponseDTO;
import com.example.myflower.dto.order.responses.OrderByWalletResponseDTO;
import com.example.myflower.exception.account.AccountAppException;
import com.example.myflower.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@CrossOrigin("*")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/add-balance")
    public ResponseEntity<AddBalanceResponseDTO> addBalance(@Valid @RequestBody AddBalanceRequestDTO addBalanceRequestDTO) {
        return accountService.addBalance(addBalanceRequestDTO);
    }

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
}
