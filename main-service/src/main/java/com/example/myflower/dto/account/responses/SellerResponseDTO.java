package com.example.myflower.dto.account.responses;

import com.example.myflower.entity.enumType.AccountGenderEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SellerResponseDTO {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private AccountGenderEnum gender;
    private String avatar;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Double ratingAverage;
    private Integer ratingCount;
    private Integer productCount;
}
