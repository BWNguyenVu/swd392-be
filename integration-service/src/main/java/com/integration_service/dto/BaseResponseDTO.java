package com.integration_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponseDTO {
    private String message;
    private Boolean success;
    private Integer code;
    private Object data;

    public BaseResponseDTO(String message, Boolean success, Integer code, Object data) {
        this.message = message;
        this.success = success;
        this.code = code;
        this.data = data;
    }
}
