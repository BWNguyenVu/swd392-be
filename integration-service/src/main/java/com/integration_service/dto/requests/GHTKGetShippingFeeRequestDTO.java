package com.integration_service.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GHTKGetShippingFeeRequestDTO {
    private String address;
    private String province;
    private String district;
    private String ward;
    private String pick_address;
    private String pick_province;
    private String pick_district;
    private String pick_ward;
    private int weight;
    private int value;
    private String deliver_option;
    private String[] tags;
    private String transport;
}
