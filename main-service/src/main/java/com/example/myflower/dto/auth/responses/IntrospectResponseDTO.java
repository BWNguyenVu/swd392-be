package com.example.myflower.dto.auth.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class IntrospectResponseDTO {
    boolean valid;
}
