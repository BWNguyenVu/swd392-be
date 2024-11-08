package com.example.myflower.dto.address.requests;

import lombok.*;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddressRequestDTO {
    private String recipientName;
    private String streetAddress;
    private String city;
    private String district;
    private String province;
    private String phoneNumber;
}