package com.example.myflower.controller;

import com.example.myflower.dto.account.requests.AddBalanceRequestDTO;
import com.example.myflower.dto.account.responses.AddBalanceResponseDTO;
import com.example.myflower.dto.account.responses.GetBalanceResponseDTO;
import com.example.myflower.service.IAccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@CrossOrigin("*")
public class AccountController {
    private final IAccountService accountService;

    public AccountController(IAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/add-balance")
    public ResponseEntity<AddBalanceResponseDTO> addBalance(@Valid @RequestBody AddBalanceRequestDTO addBalanceRequestDTO) {

        return accountService.addBalance(addBalanceRequestDTO);
    }

    @GetMapping("/balance")
    public ResponseEntity<GetBalanceResponseDTO> getBalance() {
        return accountService.getBalance();
    }
}
