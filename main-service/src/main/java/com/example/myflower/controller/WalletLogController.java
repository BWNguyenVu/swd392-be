package com.example.myflower.controller;

import com.example.myflower.dto.walletLog.responses.WalletLogResponseDTO;
import com.example.myflower.service.WalletLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wallet-logs")
@CrossOrigin("*")
public class WalletLogController {
    @Autowired
    private WalletLogService walletLogService;

    @GetMapping("/by-account")
    public ResponseEntity<List<WalletLogResponseDTO>> getAllWalletLogByAccount() {
        List<WalletLogResponseDTO> walletLogs = walletLogService.getAllWalletLogByAccount();
        return ResponseEntity.ok(walletLogs);
    }
}
