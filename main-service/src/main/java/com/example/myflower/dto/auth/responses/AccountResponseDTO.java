package com.example.myflower.dto.auth.responses;

import com.example.myflower.entity.enumType.AccountGenderEnum;
import com.example.myflower.entity.enumType.AccountProviderEnum;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import com.example.myflower.entity.enumType.AccountStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO implements Serializable {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private AccountGenderEnum gender;
    private AccountRoleEnum role;
    private AccountProviderEnum externalAuthType;
    private String externalAuthId;
    private String avatar;
    private BigDecimal balance;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private AccountStatusEnum status;
}
