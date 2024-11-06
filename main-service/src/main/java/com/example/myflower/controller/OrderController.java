package com.example.myflower.controller;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.order.requests.CreateOrderRequestDTO;
import com.example.myflower.dto.order.requests.GetOrderDetailsRequestDTO;
import com.example.myflower.dto.order.requests.GetReportRequestDTO;
import com.example.myflower.dto.order.requests.UpdateOrderDetailRequestDTO;
import com.example.myflower.dto.order.responses.OrderDetailResponseDTO;
import com.example.myflower.dto.order.responses.OrderResponseDTO;
import com.example.myflower.dto.order.responses.ReportResponseDTO;
import com.example.myflower.entity.enumType.OrderDetailsStatusEnum;
import com.example.myflower.exception.order.OrderAppException;
import com.example.myflower.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin("**")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasAuthority('USER')")
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

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/by-cod")
    public ResponseEntity<OrderResponseDTO> orderByCod(@RequestBody CreateOrderRequestDTO order) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(orderService.orderByCod(order));
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
    @GetMapping("/by-buyer")
    public ResponseEntity<BaseResponseDTO> getOrdersByUser(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false) List<OrderDetailsStatusEnum> status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {

        try {
            GetOrderDetailsRequestDTO requestDTO = GetOrderDetailsRequestDTO.builder()
                    .search(search)
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .sortBy(sortBy)
                    .order(order)
                    .status(status)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();
            Page<OrderDetailResponseDTO> responseDTOS = orderService.getAllOrderByBuyer(requestDTO);

            return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDTO.builder()
                    .message("Get orders of buyer successfully")
                    .success(true)
                    .data(responseDTOS)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDTO.builder()
                    .message(e.getCause().getMessage())
                    .success(false)
                    .build());
        }
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/by-seller")
    public ResponseEntity<BaseResponseDTO> getOrdersBySeller(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false) List<OrderDetailsStatusEnum> status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        GetOrderDetailsRequestDTO requestDTO = GetOrderDetailsRequestDTO.builder()
                .search(search)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .order(order)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        Page<OrderDetailResponseDTO> responseDTOS = orderService.getOrdersBySeller(requestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDTO.builder()
                .message("Get orders of seller successfully")
                .success(true)
                .data(responseDTOS)
                .build());
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/{orderId}")
    public ResponseEntity<BaseResponseDTO> getOrderById(@PathVariable Integer orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                        BaseResponseDTO.builder()
                                .message("Get order by id successfully")
                                .success(true)
                                .data(orderService.getOrderDetailById(orderId))
                                .build()
        );
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @PatchMapping("/update/{orderDetailId}")
    public ResponseEntity<BaseResponseDTO> updateOrderDetailById(
            @PathVariable Integer orderDetailId,
            @RequestBody UpdateOrderDetailRequestDTO requestDTO) {

        OrderDetailResponseDTO responseDTO = orderService.updateOrder(requestDTO, orderDetailId);
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDTO.builder()
                        .message("Update order detail successfully")
                        .success(true)
                        .data(responseDTO)
                        .build()
        );
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/dashboard/report")
    public ResponseEntity<BaseResponseDTO> getReport(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
            )
    {
        GetReportRequestDTO requestDTO = GetReportRequestDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
        ReportResponseDTO responseDTO = orderService.getReportByAccount(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDTO.builder()
                        .message("Get report successfully")
                        .success(true)
                        .data(responseDTO)
                        .build()
        );
    }
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/dashboard/line-chart")
    public List<Map<String, Object>> getPriceOverTimeBySeller(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return orderService.getPriceOverTimeBySellerAndDateRange(startDate, endDate);
    }

    @GetMapping("/count-status")
    public ResponseEntity<BaseResponseDTO> getOrderStatus()
    {
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDTO.builder()
                        .message("Count order status successfully")
                        .success(true)
                        .data(orderService.getCountOrderStatus())
                        .build()
        );
    }

}
