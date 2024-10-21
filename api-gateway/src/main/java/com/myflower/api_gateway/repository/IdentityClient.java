package com.myflower.api_gateway.repository;

import com.myflower.api_gateway.dto.BaseResponseDTO;
import com.myflower.api_gateway.dto.requests.IntrospectRequestDTO;
import com.myflower.api_gateway.dto.responses.IntrospectResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface IdentityClient {
    @PostExchange(url = "/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<BaseResponseDTO<IntrospectResponseDTO>> introspect(@RequestBody IntrospectRequestDTO request);
}
