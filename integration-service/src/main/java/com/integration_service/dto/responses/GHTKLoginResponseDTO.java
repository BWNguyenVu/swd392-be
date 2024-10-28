package com.integration_service.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GHTKLoginResponseDTO {
    private boolean success;
    private String message;
    private Data data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private String shop_code;
        private String shop_id;
        private int shop_order;
        private int staffId;
        private String source;
        private String role;
        private int shop_status_id;
        private int shop_type;
        private String access_token;
        private String jwt;
        private String shop_token;
    }
}
