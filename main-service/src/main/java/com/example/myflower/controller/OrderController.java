package com.example.myflower.controller;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.order.requests.CreateOrderRequestDTO;
import com.example.myflower.dto.order.requests.GetOrderDetailsBySellerRequestDTO;
import com.example.myflower.dto.order.requests.UpdateOrderDetailRequestDTO;
import com.example.myflower.dto.order.responses.OrderDetailResponseDTO;
import com.example.myflower.dto.order.responses.OrderResponseDTO;
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

@RestController
@RequestMapping("/orders")
@CrossOrigin("**")
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

//    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
//    @GetMapping("/by-account")
//    public ResponseEntity<BaseResponseDTO> getOrderByAccount(
//            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
//            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
//            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
//            @RequestParam(required = false, defaultValue = "asc") String order,
//            @RequestParam(required = false) List<OrderDetailsStatusEnum> status,
//            @RequestParam(required = false) LocalDate startDate,
//            @RequestParam(required = false) LocalDate endDate) {
//        try {
//            GetOrderByAccountRequestDTO requestDTO = GetOrderByAccountRequestDTO.builder()
//                    .pageNumber(pageNumber)
//                    .pageSize(pageSize)
//                    .sortBy(sortBy)
//                    .order(order)
//                    .status(status)
//                    .startDate(startDate)
//                    .endDate(endDate)
//                    .build();
//            Page<OrderResponseDTO> orderResponseDTOS = orderService.getAllOrderByAccount(requestDTO);
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    BaseResponseDTO.builder()
//                            .message("Get orders successful")
//                            .success(true)
//                            .data(orderResponseDTOS)
//                            .build()
//            );
//        } catch (OrderAppException e) {
//            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(BaseResponseDTO.builder()
//                            .success(false)
//                            .message(e.getErrorCode().getMessage())
//                            .code(e.getErrorCode().getCode())
//                    .build());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BaseResponseDTO.builder()
//                    .success(false)
//                    .message(e.getMessage())
//                    .build());
//        }
//    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/by-account")
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
        GetOrderDetailsBySellerRequestDTO requestDTO = GetOrderDetailsBySellerRequestDTO.builder()
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
}
