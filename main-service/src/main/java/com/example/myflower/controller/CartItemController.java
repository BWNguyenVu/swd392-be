package com.example.myflower.controller;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.cart.requests.InsertUpdateFlowerToCartRequestDTO;
import com.example.myflower.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @PostMapping("/insert-update")
    public ResponseEntity<BaseResponseDTO> insertUpdateFlowerToCart(@RequestBody InsertUpdateFlowerToCartRequestDTO request) throws Exception {
        return cartItemService.insertUpdateFlowerToCart(request);
    }

    @GetMapping("/get-by-user")
    public ResponseEntity<BaseResponseDTO> getCartItemsByUser() throws Exception {
        return cartItemService.getCartItemsByUser();
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<BaseResponseDTO> removeFlowerFromCart(@PathVariable Integer id) throws Exception {
        return cartItemService.removeFlowerFromCart(id);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<BaseResponseDTO> clearCart() throws Exception {
        return cartItemService.clearCart();
    }
}
