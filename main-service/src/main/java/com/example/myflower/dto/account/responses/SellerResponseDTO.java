package com.example.myflower.dto.account.responses;

import com.example.myflower.entity.enumType.AccountGenderEnum;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class SellerResponseDTO {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private AccountGenderEnum gender;
    private String avatar;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Double rating;
    private Long ratingCount;
    private Integer productCount;
}
