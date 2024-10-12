package com.example.myflower.service.impl;

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
import com.example.myflower.service.CartItemService;
import com.example.myflower.service.StorageService;
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
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final FlowerListingRepository flowerListingRepository;
    private final AccountRepository accountRepository;
    private final StorageService storageService;

    @Override
    public ResponseEntity<BaseResponseDTO> getCartItemsByUser() throws Exception {
        try {
            Account currentAccount = AccountUtils.getCurrentAccount();
            if (currentAccount == null) {
                return new ResponseEntity<>(new BaseResponseDTO(ErrorCode.NOT_LOGIN.getMessage(), false, HttpStatus.UNAUTHORIZED.value(), null), HttpStatus.UNAUTHORIZED);
            }

            List<CartItem> cartItems = cartItemRepository.findAllByUser(currentAccount);
            List<CartItemResponseDTO> cartItemResponse = cartItems.stream()
                    .map(CartItemResponseDTO::new)
                    .toList();
            cartItemResponse.forEach(cartItemResponseDTO -> cartItemResponseDTO.setFlowerImageUrl(storageService.getFileUrl(cartItemResponseDTO.getFlowerImageUrl())));

            return new ResponseEntity<>(new BaseResponseDTO("OK", null, HttpStatus.OK.value(),
                    cartItemResponse), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(new BaseResponseDTO("e.getMessage()", false, HttpStatus.INTERNAL_SERVER_ERROR.value(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponseDTO> insertUpdateFlowerToCart(InsertUpdateFlowerToCartRequestDTO request) throws Exception {
        try {
            Account currentAccount = AccountUtils.getCurrentAccount();
            if (currentAccount == null)
                return new ResponseEntity<>(new BaseResponseDTO(ErrorCode.NOT_LOGIN.getMessage(), false, HttpStatus.UNAUTHORIZED.value(), null), HttpStatus.UNAUTHORIZED);

            FlowerListing existingFlowerListing = flowerListingRepository.findById(request.getFlowerListingId())
                    .orElse(null);

            if (existingFlowerListing == null)
                return new ResponseEntity<>(new BaseResponseDTO(
                        ErrorCode.FLOWER_NOT_FOUND.getMessage(), false, HttpStatus.BAD_REQUEST.value(), null),
                        HttpStatus.BAD_REQUEST);

            if (!existingFlowerListing.getStatus().equals(FlowerListingStatusEnum.APPROVED))
                return new ResponseEntity<>(new BaseResponseDTO(
                        ErrorCode.FLOWER_NOT_APPROVED.getMessage(), false, HttpStatus.BAD_REQUEST.value(), null),
                        HttpStatus.BAD_REQUEST);

            if (existingFlowerListing.getStockQuantity() < request.getQuantity())
                return new ResponseEntity<>(new BaseResponseDTO(
                        ErrorCode.FLOWER_OUT_OF_STOCK.getMessage(), false, HttpStatus.BAD_REQUEST.value(), null),
                        HttpStatus.BAD_REQUEST);

            CartItem existingCartItem = cartItemRepository.findByUserAndFlower(currentAccount, existingFlowerListing);

            if (existingCartItem == null) {
                if (request.getQuantity() < 0)
                return new ResponseEntity<>(new BaseResponseDTO(
                        ErrorCode.QUANTITY_INVALID.getMessage(), false, HttpStatus.BAD_REQUEST.value(), null),
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
                response.setFlowerImageUrl(storageService.getFileUrl(response.getFlowerImageUrl()));
                return new ResponseEntity<>(new BaseResponseDTO("OK", true, HttpStatus.OK.value(),
                        response), HttpStatus.OK);

            }else {
                existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
                existingCartItem.setUpdatedAt(LocalDateTime.now());
                if (existingCartItem.getQuantity() <= 0) {
                    cartItemRepository.delete(existingCartItem);
                    return new ResponseEntity<>(new BaseResponseDTO("Ok", true, HttpStatus.OK.value(), null), HttpStatus.OK);
                }
                cartItemRepository.save(existingCartItem);
                CartItemResponseDTO response = new CartItemResponseDTO(existingCartItem);
                response.setFlowerImageUrl(storageService.getFileUrl(response.getFlowerImageUrl()));

                return new ResponseEntity<>(new BaseResponseDTO("Ok", true, HttpStatus.OK.value(),
                        response), HttpStatus.OK);
            }

        }catch (Exception e){
            return new ResponseEntity<>(new BaseResponseDTO(e.getMessage(), false, HttpStatus.INTERNAL_SERVER_ERROR.value(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponseDTO> removeFlowerFromCart(Integer id) throws Exception {
        Account currentAccount = AccountUtils.getCurrentAccount();
        if (currentAccount == null)
            return new ResponseEntity<>(new BaseResponseDTO(ErrorCode.NOT_LOGIN.getMessage(), false, HttpStatus.UNAUTHORIZED.value(), null), HttpStatus.UNAUTHORIZED);

        if (id == null)
            return new ResponseEntity<>(new BaseResponseDTO("Id is required", false, HttpStatus.BAD_REQUEST.value(), null), HttpStatus.BAD_REQUEST);

        CartItem existingCartItem = cartItemRepository.findById(id)
                .orElse(null);

        if (existingCartItem == null)
            return new ResponseEntity<>(new BaseResponseDTO("Cart item not found", false, HttpStatus.NOT_FOUND.value(), null), HttpStatus.NOT_FOUND);

        cartItemRepository.delete(existingCartItem);
        return new ResponseEntity<>(new BaseResponseDTO("Ok", null, HttpStatus.OK.value(), null), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponseDTO> clearCart() throws Exception {
        Account currentAccount = AccountUtils.getCurrentAccount();
        if (currentAccount == null)
            return new ResponseEntity<>(new BaseResponseDTO(ErrorCode.NOT_LOGIN.getMessage(), false, HttpStatus.UNAUTHORIZED.value(), null), HttpStatus.UNAUTHORIZED);

        cartItemRepository.deleteAllByUser(currentAccount);
        return new ResponseEntity<>(new BaseResponseDTO("Ok", null, HttpStatus.OK.value(), null), HttpStatus.OK);
    }

}
