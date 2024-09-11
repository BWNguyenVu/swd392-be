package com.example.myflower.dto.auth.requests;

import com.example.myflower.entity.enumType.AccountGenderEnum;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
    private String email;
    private String password;
    private String name;
    private AccountRoleEnum accountRoleEnum;
    private AccountGenderEnum accountGenderEnum;
}
