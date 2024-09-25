package com.example.myflower.service;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.cart.requests.InsertUpdateFlowerToCartRequestDTO;
import com.example.myflower.dto.cart.responses.CartItemResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.CartItem;
import com.example.myflower.entity.FlowerListing;
import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.repository.AccountRepository;
import com.example.myflower.repository.CartItemRepository;
import com.example.myflower.repository.FlowerListingRepository;
import com.example.myflower.service.interfaces.ICartItemService;
import com.example.myflower.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {
    private final CartItemRepository cartItemRepository;
    private final FlowerListingRepository flowerListingRepository;
    private final AccountRepository accountRepository;

    @Override
    public ResponseEntity<BaseResponseDTO> getCartItemsByUser() throws Exception {
        try {
            Account currentAccount = AccountUtils.getCurrentAccount();
            if (currentAccount == null) {
                return new ResponseEntity<>(new BaseResponseDTO(ErrorCode.NOT_LOGIN.getMessage(), "Unauthorized", HttpStatus.UNAUTHORIZED.value(), null), HttpStatus.UNAUTHORIZED);
            }

            List<CartItem> cartItems = cartItemRepository.findAllByUser(currentAccount);
            List<CartItemResponseDTO> cartItemResponse = cartItems.stream()
                    .map(CartItemResponseDTO::new)
                    .toList();

            return new ResponseEntity<>(new BaseResponseDTO("OK", null, HttpStatus.OK.value(),
                    cartItemResponse), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(new BaseResponseDTO("Error", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponseDTO> insertUpdateFlowerToCart(InsertUpdateFlowerToCartRequestDTO request) throws Exception {
        try {
            Account currentAccount = AccountUtils.getCurrentAccount();
            if (currentAccount == null)
                return new ResponseEntity<>(new BaseResponseDTO(ErrorCode.NOT_LOGIN.getMessage(), "Unauthorized", HttpStatus.UNAUTHORIZED.value(), null), HttpStatus.UNAUTHORIZED);

            FlowerListing existingFlowerListing = flowerListingRepository.findById(request.getFlowerListingId())
                    .orElse(null);

            if (existingFlowerListing == null)
                return new ResponseEntity<>(new BaseResponseDTO(
                        "Error", "Flower listing not found", HttpStatus.BAD_REQUEST.value(), null),
                        HttpStatus.BAD_REQUEST);

            if (!existingFlowerListing.getStatus().equals(FlowerListingStatusEnum.APPROVED))
                return new ResponseEntity<>(new BaseResponseDTO(
                        "Error", "Flower listing is not approved", HttpStatus.BAD_REQUEST.value(), null),
                        HttpStatus.BAD_REQUEST);

            if (existingFlowerListing.getStockBalance() < request.getQuantity())
                return new ResponseEntity<>(new BaseResponseDTO(
                        "Error", "Flowers are out of stock", HttpStatus.BAD_REQUEST.value(), null),
                        HttpStatus.BAD_REQUEST);

            CartItem existingCartItem = cartItemRepository.findByUserAndFlower(currentAccount, existingFlowerListing);

            if (existingCartItem == null) {
                if (request.getQuantity() < 0)
                return new ResponseEntity<>(new BaseResponseDTO(
                        "Error", "Quantity must be greater than 0", HttpStatus.BAD_REQUEST.value(), null),
                        HttpStatus.BAD_REQUEST);

                CartItem newCartItem = CartItem.builder()
                        .user(currentAccount)
                        .flower(existingFlowerListing)
                        .quantity(request.getQuantity())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                cartItemRepository.save(newCartItem);

                CartItemResponseDTO response = new CartItemResponseDTO(newCartItem);
                return new ResponseEntity<>(new BaseResponseDTO("OK", null, HttpStatus.OK.value(),
                        response), HttpStatus.OK);

            }else {
                existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
                existingCartItem.setUpdatedAt(LocalDateTime.now());
                if (existingCartItem.getQuantity() <= 0) {
                    cartItemRepository.delete(existingCartItem);
                    return new ResponseEntity<>(new BaseResponseDTO("Ok", null, HttpStatus.OK.value(), null), HttpStatus.OK);
                }
                cartItemRepository.save(existingCartItem);
                CartItemResponseDTO response = new CartItemResponseDTO(existingCartItem);

                return new ResponseEntity<>(new BaseResponseDTO("Ok", null, HttpStatus.OK.value(),
                        response), HttpStatus.OK);
            }

        }catch (Exception e){
            return new ResponseEntity<>(new BaseResponseDTO("Error", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponseDTO> removeFlowerFromCart(Integer id) throws Exception {
        Account currentAccount = AccountUtils.getCurrentAccount();
        if (currentAccount == null)
            return new ResponseEntity<>(new BaseResponseDTO(ErrorCode.NOT_LOGIN.getMessage(), "Unauthorized", HttpStatus.UNAUTHORIZED.value(), null), HttpStatus.UNAUTHORIZED);

        if (id == null)
            return new ResponseEntity<>(new BaseResponseDTO("Error", "Id is required", HttpStatus.BAD_REQUEST.value(), null), HttpStatus.BAD_REQUEST);

        CartItem existingCartItem = cartItemRepository.findById(id)
                .orElse(null);

        if (existingCartItem == null)
            return new ResponseEntity<>(new BaseResponseDTO("Error", "Cart item not found", HttpStatus.NOT_FOUND.value(), null), HttpStatus.NOT_FOUND);

        cartItemRepository.delete(existingCartItem);
        return new ResponseEntity<>(new BaseResponseDTO("Ok", null, HttpStatus.OK.value(), null), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponseDTO> clearCart() throws Exception {
        Account currentAccount = AccountUtils.getCurrentAccount();
        if (currentAccount == null)
            return new ResponseEntity<>(new BaseResponseDTO(ErrorCode.NOT_LOGIN.getMessage(), "Unauthorized", HttpStatus.UNAUTHORIZED.value(), null), HttpStatus.UNAUTHORIZED);

        cartItemRepository.deleteAllByUser(currentAccount);
        return new ResponseEntity<>(new BaseResponseDTO("Ok", null, HttpStatus.OK.value(), null), HttpStatus.OK);
    }

}
