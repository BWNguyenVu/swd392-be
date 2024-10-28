package com.integration_service.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountIntegrationLoginRequest {
    private String username;
    private String password;
    private Boolean newVersion;
}
