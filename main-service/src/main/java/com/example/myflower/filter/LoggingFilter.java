package com.example.myflower.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Component
public class LoggingFilter extends OncePerRequestFilter {
    protected static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws  ServletException, IOException {
        MDC.put("traceId", UUID.randomUUID().toString());
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(new RequestWrapper(request), new ResponseWrapper(response), filterChain);
        }
        MDC.clear();
    }

    protected void doFilterWrapped(RequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        try {
            logRequest(request);
            filterChain.doFilter(request, response);
        } finally {
            logResponse(response);
            response.copyBodyToResponse();
        }
    }

    private static void logRequest(RequestWrapper request) throws IOException {
        StringBuilder message = new StringBuilder();
        message.append("\n========= Request Begin =========\n");
        message.append("IP: ").append(request.getRemoteAddr()).append("\n");
        message.append("RequestID: ").append(request.getRequestId()).append("\n");
        HttpSession session = request.getSession(false);
        if (session != null) {
            message.append("SessionID: ").append(session.getId()).append("\n");
        }
        if (request.getMethod() != null) {
            message.append("Method: ").append(request.getMethod()).append("\n");
        }
        if (request.getRequestURI() != null) {
            message.append("URI: ").append(request.getRequestURI()).append("\n");
        }
        if (request.getQueryString() != null) {
            message.append("Query strings: ").append(request.getQueryString()).append("\n");
        }
        if (!request.getRequestURI().contains("/auth/login")
                && !request.getRequestURI().contains("/auth/register")
                && !request.getRequestURI().contains("change-password")
                && !request.getRequestURI().contains("reset-password") && !isMultiPart(request)) {
            byte[] content = StreamUtils.copyToByteArray(request.getInputStream());
            if (content.length > 0) {
                message.append(new String(content));
            }
        }

        message.append("========= Request End =========");
        log.info(message.toString());
    }

    private static void logResponse(ContentCachingResponseWrapper response) throws IOException {
        logPayload("\nResponse", response.getContentType(), response.getContentInputStream());
    }

    private static void logPayload(String prefix, String contentType, InputStream inputStream) throws IOException {
        boolean visible = isVisible(MediaType.valueOf(contentType == null ? "application/json" : contentType));
        if (visible) {
            byte[] content = StreamUtils.copyToByteArray(inputStream);
            if (content.length > 0) {
                String contentString = new String(content);
                log.info("{} Payload: {}", prefix, contentString);
            }
        } else {
            log.info("{} Payload: Binary Content", prefix);
        }
    }

    private static boolean isVisible(MediaType mediaType) {
        final List<MediaType> VISIBLE_TYPES = Arrays.asList(
                MediaType.valueOf("text/*"),
                MediaType.APPLICATION_FORM_URLENCODED,
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML,
                MediaType.valueOf("application/*+json"),
                MediaType.valueOf("application/*+xml"),
                MediaType.MULTIPART_FORM_DATA
        );

        return VISIBLE_TYPES.stream()
                .anyMatch(visibleType -> visibleType.includes(mediaType));
    }

    private static boolean isMultiPart(HttpServletRequest request) {
        return request.getContentType() != null
                && request.getContentType().startsWith("multipart/form-data");
    }
}
