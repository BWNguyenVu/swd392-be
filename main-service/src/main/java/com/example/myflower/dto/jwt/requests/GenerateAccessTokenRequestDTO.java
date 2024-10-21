package com.example.myflower.dto.jwt.requests;

import com.example.myflower.entity.enumType.AccountRoleEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GenerateAccessTokenRequestDTO {
    private String email;
    private AccountRoleEnum role;
    private Integer userId;
}
