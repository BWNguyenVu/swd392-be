package com.myflower.api_gateway.services;

import com.myflower.api_gateway.dto.BaseResponseDTO;
import com.myflower.api_gateway.dto.responses.IntrospectResponseDTO;
import reactor.core.publisher.Mono;

public interface IdentityService {
    Mono<BaseResponseDTO<IntrospectResponseDTO>> introspect(String token);
}
