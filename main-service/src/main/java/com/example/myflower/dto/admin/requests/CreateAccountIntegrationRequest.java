package com.example.myflower.dto.admin.requests;

import com.example.myflower.entity.enumType.AccountProviderEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountIntegrationRequest {

    @NotBlank(message = "Username must not be blank")
    private String username;

    @NotBlank(message = "Password must not be blank")
    private String password;

    @NotNull(message = "New version flag must not be null")
    private Boolean newVersion;

    @NotNull(message = "Provider must not be null")
    private AccountProviderEnum provider;
}
