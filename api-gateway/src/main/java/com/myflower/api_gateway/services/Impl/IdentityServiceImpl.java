package com.myflower.api_gateway.services.Impl;

import com.myflower.api_gateway.dto.BaseResponseDTO;
import com.myflower.api_gateway.dto.requests.IntrospectRequestDTO;
import com.myflower.api_gateway.dto.responses.IntrospectResponseDTO;
import com.myflower.api_gateway.repository.IdentityClient;
import com.myflower.api_gateway.services.IdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class IdentityServiceImpl implements IdentityService {
    @Autowired
    IdentityClient identityClient;

    @Override
    public Mono<BaseResponseDTO<IntrospectResponseDTO>> introspect(String token){
        return identityClient.introspect(IntrospectRequestDTO.builder()
                        .token(token)
                .build());
    }
}
