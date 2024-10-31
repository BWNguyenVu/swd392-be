package com.example.myflower.service;

import com.example.myflower.dto.order.requests.CreateOrderRequestDTO;
import com.example.myflower.dto.order.requests.GetOrderDetailsRequestDTO;
import com.example.myflower.dto.order.requests.GetReportRequestDTO;
import com.example.myflower.dto.order.requests.UpdateOrderDetailRequestDTO;
import com.example.myflower.dto.order.responses.CountOrderStatusResponseDTO;
import com.example.myflower.dto.order.responses.OrderDetailResponseDTO;
import com.example.myflower.dto.order.responses.OrderResponseDTO;
import com.example.myflower.dto.order.responses.ReportResponseDTO;
import com.example.myflower.exception.order.OrderAppException;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderService {
    OrderResponseDTO orderByWallet(CreateOrderRequestDTO orderDTO) throws OrderAppException;
    OrderResponseDTO orderByCod(CreateOrderRequestDTO orderDTO) throws OrderAppException;
    List<OrderDetailResponseDTO> getAllOrderDetailsByOrderSummaryId(Integer orderSummaryId) throws OrderAppException;
    Page<OrderDetailResponseDTO> getAllOrderByBuyer(GetOrderDetailsRequestDTO requestDTO);
    Page<OrderDetailResponseDTO> getOrdersBySeller(GetOrderDetailsRequestDTO requestDTO) throws OrderAppException;
    OrderDetailResponseDTO updateOrder(UpdateOrderDetailRequestDTO requestDTO, Integer orderDetailId);
    OrderDetailResponseDTO getOrderDetailById(Integer orderDetailId);
    ReportResponseDTO getReportByAccount(GetReportRequestDTO requestDTO);
    List<Map<String, Object>> getPriceOverTimeBySellerAndDateRange(LocalDate startDate, LocalDate endDate);
    CountOrderStatusResponseDTO getCountOrderStatus();
}
