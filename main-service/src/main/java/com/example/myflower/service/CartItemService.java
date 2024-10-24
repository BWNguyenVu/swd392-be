package com.example.myflower.service;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.cart.requests.InsertUpdateFlowerToCartRequestDTO;
import com.example.myflower.dto.order.requests.GetReportRequestDTO;
import com.example.myflower.entity.Account;
import org.springframework.http.ResponseEntity;

public interface CartItemService {
    ResponseEntity<BaseResponseDTO> getCartItemsByUser() throws Exception;
    ResponseEntity<BaseResponseDTO> insertUpdateFlowerToCart(InsertUpdateFlowerToCartRequestDTO request) throws Exception;
    ResponseEntity<BaseResponseDTO> removeFlowerFromCart(Integer id) throws Exception;
    ResponseEntity<BaseResponseDTO> clearCart() throws Exception;
    Integer countCartByTime(GetReportRequestDTO requestDTO, Account seller);
}
