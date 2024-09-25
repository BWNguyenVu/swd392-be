package com.example.myflower.service.interfaces;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.cart.requests.InsertUpdateFlowerToCartRequestDTO;
import org.springframework.http.ResponseEntity;

public interface ICartItemService {
    ResponseEntity<BaseResponseDTO> getCartItemsByUser() throws Exception;
    ResponseEntity<BaseResponseDTO> insertUpdateFlowerToCart(InsertUpdateFlowerToCartRequestDTO request) throws Exception;
    ResponseEntity<BaseResponseDTO> removeFlowerFromCart(Integer id) throws Exception;
    ResponseEntity<BaseResponseDTO> clearCart() throws Exception;
}
