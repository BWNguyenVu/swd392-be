package com.example.myflower.dto.account.responses;

import com.example.myflower.entity.enumType.AccountGenderEnum;
import com.example.myflower.entity.enumType.AccountProviderEnum;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import com.example.myflower.entity.enumType.AccountStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponseDTO {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private AccountGenderEnum gender;
    private AccountRoleEnum role;
    private AccountProviderEnum externalAuthType;
    @Column(nullable = true, length = 1000)
    private String avatar;
    private AccountStatusEnum status;
    private BigDecimal balance;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
