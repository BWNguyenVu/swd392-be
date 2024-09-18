package com.example.myflower.dto.auth.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequestDTO {
    private String oldPassword;
    private String newPassword;
    private String repeatPassword;
}
