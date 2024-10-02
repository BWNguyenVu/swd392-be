package com.example.myflower.controller;

import com.example.myflower.dto.account.requests.AddBalanceRequestDTO;
import com.example.myflower.dto.account.responses.AddBalanceResponseDTO;
import com.example.myflower.dto.account.responses.GetBalanceResponseDTO;
import com.example.myflower.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
}
