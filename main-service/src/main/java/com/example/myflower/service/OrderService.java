package com.example.myflower.service;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.order.requests.CreateOrderRequestDTO;
import com.example.myflower.dto.order.responses.OrderDetailResponseDTO;
import com.example.myflower.dto.order.responses.OrderResponseDTO;
import com.example.myflower.exception.order.OrderAppException;

import java.util.List;

public interface OrderService {
    OrderResponseDTO orderByWallet(CreateOrderRequestDTO orderDTO) throws OrderAppException;
    List<OrderDetailResponseDTO> getAllOrderDetailsByOrderSummaryId(Integer orderSummaryId) throws OrderAppException;
    BaseResponseDTO getAllOrderByAccount();
    BaseResponseDTO getOrdersBySeller() throws OrderAppException;
}
