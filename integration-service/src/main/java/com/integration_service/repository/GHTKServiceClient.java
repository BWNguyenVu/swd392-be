package com.integration_service.repository;

import com.integration_service.dto.requests.GHTKGetShippingFeeRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ghtk-service-api", url = "https://services.giaohangtietkiem.vn/services/shipment/fee")
public interface GHTKServiceClient {

    @GetMapping
    ResponseEntity<?> getShippingFee(
            @RequestParam("address") String address,
            @RequestParam("province") String province,
            @RequestParam("district") String district,
            @RequestParam("ward") String ward,
            @RequestParam("pick_address") String pickAddress,
            @RequestParam("pick_province") String pickProvince,
            @RequestParam("pick_district") String pickDistrict,
            @RequestParam("pick_ward") String pickWard,
            @RequestParam("weight") int weight,
            @RequestParam("value") int value,
            @RequestParam("deliver_option") String deliverOption,
            @RequestParam("tags[]") String[] tags,
            @RequestParam("transport") String transport,
            @RequestHeader("Token") String token
    );
}
