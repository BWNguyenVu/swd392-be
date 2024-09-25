package com.example.myflower.controller;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.cart.requests.InsertUpdateFlowerToCartRequestDTO;
import com.example.myflower.service.interfaces.ICartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartItemController {
    private final ICartItemService cartItemService;

    // them moi hoac cap nhat flower trong gio hang
    @PostMapping("insert-update")
    public ResponseEntity<BaseResponseDTO> insertUpdateFlowerToCart(@RequestBody InsertUpdateFlowerToCartRequestDTO request) throws Exception {
        return cartItemService.insertUpdateFlowerToCart(request);
    }

    // lay tat ca cac flower trong gio hang cua user
    @GetMapping("get-by-user")
    public ResponseEntity<BaseResponseDTO> getCartItemsByUser() throws Exception {
        return cartItemService.getCartItemsByUser();
    }

    // xoa flower khoi gio hang
    @DeleteMapping("remove/{id}")
    public ResponseEntity<BaseResponseDTO> removeFlowerFromCart(@PathVariable Integer id) throws Exception {
        return cartItemService.removeFlowerFromCart(id);
    }

    // xoa toan bo gio hang
    @DeleteMapping("clear")
    public ResponseEntity<BaseResponseDTO> clearCart() throws Exception {
        return cartItemService.clearCart();
    }
}
