package com.example.myflower.dto.cart.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertUpdateFlowerToCartRequestDTO {
    private Integer flowerListingId;
    private Integer quantity;
}
