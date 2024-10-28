package com.integration_service.scheduler;

import com.integration_service.dto.requests.AccountIntegrationLoginRequest;
import com.integration_service.dto.responses.AccountIntegrationResponse;
import com.integration_service.dto.responses.GHTKLoginResponseDTO;
import com.integration_service.entity.enumType.AccountProviderEnum;
import com.integration_service.service.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@EnableScheduling
public class IntegrationScheduler {
    private static final Logger logger = LoggerFactory.getLogger(IntegrationScheduler.class);

    private final IntegrationService integrationService;
    private String jwt = null;
    private String shopToken = null;

    @Autowired
    public IntegrationScheduler(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 2)
    public void scheduled() {
        try {
            AccountIntegrationResponse accountIntegrationResponse = integrationService.getAccountIntegrationByProvider(AccountProviderEnum.GHTK);
            if (accountIntegrationResponse == null) {
                logger.warn("No account integration found for provider: GHTK");
                return;
            }

            AccountIntegrationLoginRequest request = AccountIntegrationLoginRequest.builder()
                    .username(accountIntegrationResponse.getUsername())
                    .newVersion(accountIntegrationResponse.getNewVersion())
                    .password(accountIntegrationResponse.getPassword())
                    .build();

            GHTKLoginResponseDTO responseDTO = integrationService.getLoginGHTKResponse(request);
            if (responseDTO != null && responseDTO.getData() != null) {
                jwt = responseDTO.getData().getJwt();
                shopToken = responseDTO.getData().getShop_token();
                logger.info("Tokens updated successfully.");
            } else {
                logger.warn("Login response from GHTK was null or empty.");
            }
        } catch (Exception e) {
            logger.error("Error during token refresh", e);
        }
    }

    public String getJwt() {
        return "Bearer " + jwt;
    }

    public String getShopToken() {
        return shopToken;
    }
}
