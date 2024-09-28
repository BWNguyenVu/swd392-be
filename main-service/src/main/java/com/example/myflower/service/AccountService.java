package com.example.myflower.service;

import com.example.myflower.dto.account.requests.AddBalanceRequestDTO;
import com.example.myflower.dto.account.responses.AddBalanceResponseDTO;
import com.example.myflower.dto.account.responses.GetBalanceResponseDTO;
import org.springframework.http.ResponseEntity;

public interface AccountService {
    ResponseEntity<AddBalanceResponseDTO> addBalance(AddBalanceRequestDTO addBalanceRequestDTO);
    ResponseEntity<GetBalanceResponseDTO> getBalance();
}
