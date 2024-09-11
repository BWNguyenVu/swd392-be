package com.example.myflower.dto.auth.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDTO {
    private String message;

    private String error;

    private String accessToken;

    private String refreshToken;

    public LoginResponseDTO(String message, String error, String accessToken, String refreshToken) {
        super();
        this.message = message;
        this.error = error;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
