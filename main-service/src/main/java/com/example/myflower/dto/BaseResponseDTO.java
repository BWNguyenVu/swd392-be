package com.example.myflower.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;



@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponseDTO {
    private String message;
    private String error;
    private Integer code;
    private Object result;


    public BaseResponseDTO(String message, String error, Integer code, Object result) {
        super();
        this.message = message;
        this.error = error;
        this.code = code;
        this.result = result;
    }
}
