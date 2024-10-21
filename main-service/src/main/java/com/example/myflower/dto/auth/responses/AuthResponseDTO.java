package com.example.myflower.dto.auth.responses;

import com.example.myflower.entity.enumType.AccountGenderEnum;
import com.example.myflower.entity.enumType.AccountProviderEnum;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import com.example.myflower.entity.enumType.AccountStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
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
public class AuthResponseDTO implements Serializable {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private AccountGenderEnum gender;
    private AccountRoleEnum role;
    private AccountProviderEnum externalAuthType;
    private String externalAuthId;
    @Column(nullable = true, length = 1000)
    private String avatar;
    private BigDecimal balance;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private AccountStatusEnum status;
}
