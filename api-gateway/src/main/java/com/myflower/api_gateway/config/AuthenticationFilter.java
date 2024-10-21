package com.myflower.api_gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myflower.api_gateway.dto.BaseResponseDTO;
import com.myflower.api_gateway.dto.responses.IntrospectResponseDTO;
import com.myflower.api_gateway.services.IdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {
    @Autowired
    IdentityService identityService;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.startsWith("/api/v1/auth")
                || path.startsWith("/api/v1/flowers")
                || path.startsWith("/api/v1/flower-categories")
        )
        {
            return chain.filter(exchange);
        }

        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader))
            return unauthenticated(exchange.getResponse());

        String token = authHeader.get(0).replace("Bearer ", "");

        return identityService.introspect(token).flatMap(introspectResponse -> {
            ObjectMapper objectMapper = new ObjectMapper();

            IntrospectResponseDTO introspectData = objectMapper.convertValue(
                    introspectResponse.getData(),
                    IntrospectResponseDTO.class
            );

            if (introspectData.isValid()) {
                return chain.filter(exchange);
            } else {
                return unauthenticated(exchange.getResponse());
            }
        }).onErrorResume(throwable -> unauthenticated(exchange.getResponse()));
    }
    @Override
    public int getOrder() {
        return -1;
    }
    Mono<Void> unauthenticated(ServerHttpResponse response){

        BaseResponseDTO apiResponse = BaseResponseDTO.builder()
                .code(1401)
                .message("Unauthenticated")
                .success(false)
                .build();

        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
