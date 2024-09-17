package com.example.myflower.dto.auth.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResetPasswordRequestDTO {
    private String newPassword;
    private String repeatPassword;
}
