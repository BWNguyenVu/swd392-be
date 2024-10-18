package com.example.myflower.service;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.order.requests.CreateOrderRequestDTO;
import com.example.myflower.dto.order.requests.GetOrderByAccountRequestDTO;
import com.example.myflower.dto.order.requests.GetOrderDetailsBySellerRequestDTO;
import com.example.myflower.dto.order.requests.UpdateOrderDetailRequestDTO;
import com.example.myflower.dto.order.responses.OrderDetailResponseDTO;
import com.example.myflower.dto.order.responses.OrderResponseDTO;
import com.example.myflower.exception.order.OrderAppException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    OrderResponseDTO orderByWallet(CreateOrderRequestDTO orderDTO) throws OrderAppException;
    List<OrderDetailResponseDTO> getAllOrderDetailsByOrderSummaryId(Integer orderSummaryId) throws OrderAppException;
//    Page<OrderResponseDTO> getAllOrderByAccount(GetOrderByAccountRequestDTO requestDTO) throws OrderAppException;
    Page<OrderDetailResponseDTO> getOrdersBySeller(GetOrderDetailsBySellerRequestDTO requestDTO) throws OrderAppException;
    OrderDetailResponseDTO updateOrder(UpdateOrderDetailRequestDTO requestDTO, Integer orderDetailId);
}
