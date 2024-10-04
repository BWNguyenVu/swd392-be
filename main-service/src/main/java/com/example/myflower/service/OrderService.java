package com.example.myflower.service;

import com.example.myflower.dto.order.requests.CreateOrderRequestDTO;
import com.example.myflower.dto.order.responses.OrderByWalletResponseDTO;
import com.example.myflower.exception.order.OrderAppException;

public interface OrderService {
    OrderByWalletResponseDTO orderByWallet(CreateOrderRequestDTO orderDTO) throws OrderAppException;
}
