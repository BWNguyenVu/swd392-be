package com.integration_service.controller;

import com.integration_service.dto.requests.GHTKGetShippingFeeRequestDTO;
import com.integration_service.dto.requests.GHTKParseAddressRequestDTO;
import com.integration_service.service.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("**")
@RestController
@RequestMapping("/integration")
public class IntegrationController {
    @Autowired
    private IntegrationService integrationService;

    @PostMapping("/ghtk/parse-address")
    public ResponseEntity<?> parseAddress(@RequestBody GHTKParseAddressRequestDTO requestDTO){
        return integrationService.parseAddress(requestDTO);
    }

    @GetMapping("/ghtk/fee-ship")
    public ResponseEntity<?> getFeeShip(@RequestBody GHTKGetShippingFeeRequestDTO requestDTO){
        return integrationService.getFeeShip(requestDTO);
    }
}
