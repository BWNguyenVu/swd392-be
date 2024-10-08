package com.example.myflower.controller;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@CrossOrigin("*")
public class TransactionController {
    @Autowired
    private TransactionService  transactionService;

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/by-account")
    public ResponseEntity<BaseResponseDTO> getTransactionByAccount() {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransactionByAccount());
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/{transactionId}")
    public ResponseEntity<BaseResponseDTO> getTransactionById(@PathVariable Integer transactionId) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransactionById(transactionId));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<BaseResponseDTO> softDeleteTransaction(@PathVariable Integer transactionId) {
        transactionService.softDeleteTransaction(transactionId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponseDTO.builder()
                        .message("Transaction soft deleted successfully")
                        .success(true)
                        .build());
    }

}
