package com.example.myflower.dto.account.responses;

import com.example.myflower.entity.enumType.AccountGenderEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
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
