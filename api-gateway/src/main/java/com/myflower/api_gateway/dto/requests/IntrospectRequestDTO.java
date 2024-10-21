package com.myflower.api_gateway.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class IntrospectRequestDTO {
    public String token;
}
