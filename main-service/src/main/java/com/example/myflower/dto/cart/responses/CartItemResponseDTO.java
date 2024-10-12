package com.example.myflower.dto.cart.responses;

import com.example.myflower.entity.CartItem;
import com.example.myflower.entity.FlowerListing;
import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponseDTO {
    private Integer id;
    private Integer quantity;
    // Các thuộc tính từ FlowerListing
    private Integer flowerId;
    private String flowerName;
    private String flowerDescription;
    private BigDecimal flowerPrice;
    private String flowerImageUrl;
    private String eventType;
    private Integer stockQuantity;
    private String address;
    private FlowerListingStatusEnum status;

    public CartItemResponseDTO(CartItem cartItem) {
        this.id = cartItem.getId();
        this.quantity = cartItem.getQuantity();

        if (cartItem.getFlower() != null) {
            FlowerListing flower = cartItem.getFlower();
            this.flowerId = flower.getId();
            this.flowerName = flower.getName();
            this.flowerDescription = flower.getDescription();
            this.flowerPrice = flower.getPrice();
            this.flowerImageUrl = flower.getImageUrl();
            this.eventType = flower.getEventType();
            this.stockQuantity = flower.getStockQuantity();
            this.address = flower.getAddress();
            this.status = flower.getStatus();
        }
    }
}
