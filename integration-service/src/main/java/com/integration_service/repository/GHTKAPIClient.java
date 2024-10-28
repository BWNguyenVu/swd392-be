package com.integration_service.repository;

import com.integration_service.dto.requests.AccountIntegrationLoginRequest;
import com.integration_service.dto.responses.GHTKLoginResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ghtk-api", url = "https://web.giaohangtietkiem.vn/api/v1")
public interface GHTKAPIClient {
    @PostMapping("/auth/login")
    ResponseEntity<GHTKLoginResponseDTO> login(@RequestBody AccountIntegrationLoginRequest request);
}
