package com.example.myflower.dto.account.requests;

import com.example.myflower.entity.enumType.AccountGenderEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAccountRequestDTO {
    private String name;
    private String phone;
    @Enumerated(EnumType.STRING)
    private AccountGenderEnum gender;
}
