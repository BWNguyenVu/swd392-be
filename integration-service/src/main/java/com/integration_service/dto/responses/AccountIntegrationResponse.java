package com.integration_service.dto.responses;

import com.integration_service.entity.enumType.AccountProviderEnum;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountIntegrationResponse {
    private Integer id;
    private String username;
    private String password;
    private Boolean newVersion;
    private AccountProviderEnum provider;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
