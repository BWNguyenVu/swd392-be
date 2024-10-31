package com.example.myflower.dto.account.requests;

import com.example.myflower.entity.enumType.AccountGenderEnum;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import com.example.myflower.entity.enumType.AccountStatusEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAccountRequestDTO {
    private String name;
    private String phone;
    @Enumerated(EnumType.STRING)
    private AccountGenderEnum gender;
    private AccountStatusEnum status;
}
