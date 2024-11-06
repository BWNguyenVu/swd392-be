package com.integration_service.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration_service.dto.requests.AccountIntegrationLoginRequest;
import com.integration_service.dto.requests.GHTKGetShippingFeeRequestDTO;
import com.integration_service.dto.requests.GHTKParseAddressRequestDTO;
import com.integration_service.dto.requests.GHTKSuggestAddressRequestDTO;
import com.integration_service.dto.responses.AccountIntegrationResponse;
import com.integration_service.dto.responses.GHTKLoginResponseDTO;
import com.integration_service.entity.AccountIntegration;
import com.integration_service.entity.enumType.AccountProviderEnum;
import com.integration_service.mapper.AccountIntegrationMapper;
import com.integration_service.repository.AccountIntegrationRepository;
import com.integration_service.repository.GHTKAPIClient;
import com.integration_service.repository.GHTKGatewayClient;
import com.integration_service.repository.GHTKServiceClient;
import com.integration_service.scheduler.IntegrationScheduler;
import com.integration_service.service.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IntegrationServiceImpl implements IntegrationService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AccountIntegrationRepository accountIntegrationRepository;
    @Autowired
    private GHTKAPIClient ghtkAPIClient;
    @Autowired
    private GHTKGatewayClient ghtkGatewayClient;
    @Lazy
    @Autowired
    private IntegrationScheduler integrationScheduler;
    @Autowired
    private GHTKServiceClient ghtkServiceClient;
    @Override
    public void createAccountIntegration(String account) {
        try {
            AccountIntegration accountEntity = objectMapper.readValue(account, AccountIntegration.class);
            accountEntity.setCreatedAt(LocalDateTime.now());
            accountIntegrationRepository.save(accountEntity);
            System.out.println(accountEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public AccountIntegrationResponse getAccountIntegrationByProvider(AccountProviderEnum providerEnum){
        AccountIntegration accountIntegration = accountIntegrationRepository.findAccountIntegrationByProvider(providerEnum);
        return AccountIntegrationMapper.mapToAccountIntegrationResponseDTO(accountIntegration);
    }

    @Override
    public GHTKLoginResponseDTO getLoginGHTKResponse(AccountIntegrationLoginRequest request) {
        ResponseEntity<GHTKLoginResponseDTO> responseDTO = ghtkAPIClient.login(request);
        return responseDTO.getBody();
    }

    @Override
    public ResponseEntity<?> parseAddress(GHTKParseAddressRequestDTO requestDTO) {
        try {
            String jwtToken = integrationScheduler.getJwt();
            String shopToken = integrationScheduler.getShopToken();
            return ghtkGatewayClient.parseAddress(requestDTO.getAddress(), shopToken, jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getLocalizedMessage());
        }
    }

    @Override
    public ResponseEntity<?> suggestAddress(GHTKSuggestAddressRequestDTO requestDTO) {
        try {
            String jwtToken = integrationScheduler.getJwt();
            String shopToken = integrationScheduler.getShopToken();
            return ghtkGatewayClient.suggestAddress(requestDTO.getSearch(), shopToken, jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getLocalizedMessage());
        }
    }

    @Override
    public ResponseEntity<?> getFeeShip(GHTKGetShippingFeeRequestDTO requestDTO){
        try {
            String shopToken = integrationScheduler.getShopToken();
            return ghtkServiceClient.getShippingFee(requestDTO.getAddress(), requestDTO.getProvince(), requestDTO.getDistrict(), requestDTO.getWard(), requestDTO.getPick_address(), requestDTO.getPick_province(),
                    requestDTO.getPick_district(), requestDTO.getPick_ward(), requestDTO.getWeight(), requestDTO.getValue(), requestDTO.getDeliver_option(), requestDTO.getTags(), requestDTO.getTransport(), shopToken);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getLocalizedMessage());
        }
    }
}
