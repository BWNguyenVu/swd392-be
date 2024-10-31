package com.integration_service.service;

import com.integration_service.dto.requests.AccountIntegrationLoginRequest;
import com.integration_service.dto.requests.GHTKGetShippingFeeRequestDTO;
import com.integration_service.dto.requests.GHTKParseAddressRequestDTO;
import com.integration_service.dto.requests.GHTKSuggestAddressRequestDTO;
import com.integration_service.dto.responses.AccountIntegrationResponse;
import com.integration_service.dto.responses.GHTKLoginResponseDTO;
import com.integration_service.entity.enumType.AccountProviderEnum;
import org.springframework.http.ResponseEntity;

public interface IntegrationService {
    void createAccountIntegration(String account);
    AccountIntegrationResponse getAccountIntegrationByProvider(AccountProviderEnum providerEnum);
    GHTKLoginResponseDTO getLoginGHTKResponse(AccountIntegrationLoginRequest request);
    ResponseEntity<?> parseAddress(GHTKParseAddressRequestDTO requestDTO);
    ResponseEntity<?> suggestAddress(GHTKSuggestAddressRequestDTO requestDTO);
    ResponseEntity<?> getFeeShip(GHTKGetShippingFeeRequestDTO requestDTO);
}
