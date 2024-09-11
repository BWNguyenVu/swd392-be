package com.example.myflower.dto.auth.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadAvatarResponseDTO {
    private String message;
    private String error;
    private Integer accountId;
    private String imageUrl;

    public UploadAvatarResponseDTO(String message, String error, Integer accountId, String imageUrl) {
        this.message = message;
        this.error = error;
        this.accountId = accountId;
        this.imageUrl = imageUrl;
    }
}
