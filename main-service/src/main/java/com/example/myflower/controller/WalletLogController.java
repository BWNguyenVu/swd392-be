package com.example.myflower.controller;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.walletLog.requests.GetWalletLogsRequestDTO;
import com.example.myflower.dto.walletLog.responses.WalletLogResponseDTO;
import com.example.myflower.entity.enumType.WalletLogStatusEnum;
import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import com.example.myflower.service.WalletLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/wallet-logs")
@CrossOrigin("**")
public class WalletLogController {
    @Autowired
    private WalletLogService walletLogService;

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/by-account")
    public ResponseEntity<BaseResponseDTO> getAllWalletLogByAccount(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false) List<WalletLogStatusEnum> status,
            @RequestParam(required = false) List<WalletLogTypeEnum> type,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
            )
    {

        GetWalletLogsRequestDTO getWalletLogsRequestDTO = GetWalletLogsRequestDTO.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .order(order)
                .status(status)
                .type(type)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        Page<WalletLogResponseDTO> walletLogs = walletLogService.getAllWalletLogByAccount(getWalletLogsRequestDTO);

        return ResponseEntity.ok(BaseResponseDTO.builder()
                .message("Get all wallet logs successfully")
                .success(true)
                .data(walletLogs)
                .build());
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
