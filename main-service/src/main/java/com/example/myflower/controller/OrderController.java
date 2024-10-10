package com.example.myflower.controller;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.order.requests.CreateOrderRequestDTO;
import com.example.myflower.dto.order.responses.OrderResponseDTO;
import com.example.myflower.exception.order.OrderAppException;
import com.example.myflower.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @PostMapping("/by-wallet")
    public ResponseEntity<OrderResponseDTO> orderByWallet(@RequestBody CreateOrderRequestDTO order) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(orderService.orderByWallet(order));
        } catch (OrderAppException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(
                    OrderResponseDTO.builder()
                            .message(e.getErrorCode().getMessage())
                            .error(true)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    OrderResponseDTO.builder()
                            .message(e.getMessage())
                            .error(true)
                            .build()
            );
        }
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/by-account")
    public ResponseEntity<BaseResponseDTO> getOrderByAccount() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrderByAccount());
        } catch (OrderAppException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(BaseResponseDTO.builder()
                            .success(false)
                            .message(e.getErrorCode().getMessage())
                            .code(e.getErrorCode().getCode())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BaseResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/seller")
    public ResponseEntity<BaseResponseDTO> getOrdersBySeller() {
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDTO.builder()
                .message("Get orders of seller successfully")
                .success(true)
                .data(orderService.getOrdersBySeller())
                .build());
    }
}
