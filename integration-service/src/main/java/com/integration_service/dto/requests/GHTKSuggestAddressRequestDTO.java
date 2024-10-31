package com.integration_service.dto.requests;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GHTKSuggestAddressRequestDTO {
    private String search;
}
