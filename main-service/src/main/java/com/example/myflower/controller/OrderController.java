package com.example.myflower.controller;

import com.example.myflower.dto.order.requests.CreateOrderRequestDTO;
import com.example.myflower.dto.order.responses.OrderByWalletResponseDTO;
import com.example.myflower.exception.order.OrderAppException;
import com.example.myflower.service.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
public class OrderController {
    @Autowired
    private OrderServiceImpl orderServiceImpl;


    @PostMapping("/by-wallet")
    public ResponseEntity<OrderByWalletResponseDTO> createWallet(@RequestBody CreateOrderRequestDTO order) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(orderServiceImpl.orderByWallet(order));
        } catch (OrderAppException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(
                    OrderByWalletResponseDTO.builder()
                            .message(e.getErrorCode().getMessage())
                            .error(true)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    OrderByWalletResponseDTO.builder()
                            .message(e.getMessage())
                            .error(true)
                            .build()
            );
        }
    }
}
