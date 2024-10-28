package com.integration_service.mapper;

import com.integration_service.dto.responses.AccountIntegrationResponse;
import com.integration_service.entity.AccountIntegration;

public class AccountIntegrationMapper {

    public static AccountIntegrationResponse mapToAccountIntegrationResponseDTO(AccountIntegration accountIntegration) {
        return AccountIntegrationResponse.builder()
                .id(accountIntegration.getId())
                .username(accountIntegration.getUsername())
                .password(accountIntegration.getPassword())
                .createdAt(accountIntegration.getCreatedAt())
                .updatedAt(accountIntegration.getUpdatedAt())
                .provider(accountIntegration.getProvider())
                .newVersion(accountIntegration.getNewVersion())
                .build();
    }
}
