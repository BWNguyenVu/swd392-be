package com.example.myflower.controller;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.walletLog.responses.WalletLogResponseDTO;
import com.example.myflower.service.WalletLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet-logs")
@CrossOrigin("*")
public class WalletLogController {
    @Autowired
    private WalletLogService walletLogService;


    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/by-account")
    public ResponseEntity<List<WalletLogResponseDTO>> getAllWalletLogByAccount() {
        List<WalletLogResponseDTO> walletLogs = walletLogService.getAllWalletLogByAccount();
        return ResponseEntity.ok(walletLogs);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/{walletLogId}")
    public ResponseEntity<WalletLogResponseDTO> getWalletLogById(@PathVariable Integer walletLogId) {
        WalletLogResponseDTO walletLog = walletLogService.getWalletLogById(walletLogId);
        return ResponseEntity.ok(walletLog);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @DeleteMapping("/{walletLogId}")
    public ResponseEntity<BaseResponseDTO> softDeleteWalletLog(@PathVariable Integer walletLogId) {
        walletLogService.softDeleteWalletLog(walletLogId);
        return ResponseEntity.ok(BaseResponseDTO.builder()
                .message("WalletLog soft deleted successfully")
                .success(true)
                .build());
    }

}
