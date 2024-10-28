package com.integration_service.dto.requests;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GHTKParseAddressRequestDTO {
    private String address;
}
