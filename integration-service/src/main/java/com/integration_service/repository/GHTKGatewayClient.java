package com.integration_service.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ghtk-gateway-api", url = "https://shop-gateway.ghtk.vn/fw/fancy/api/v1")
public interface GHTKGatewayClient {
    @GetMapping("/address/parse-address")
    ResponseEntity<?> parseAddress(@RequestParam String address,
                                   @RequestHeader("Token") String token,
                                   @RequestHeader("Authorization") String authorization);
}
