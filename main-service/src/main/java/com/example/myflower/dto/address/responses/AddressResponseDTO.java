package com.example.myflower.dto.address.responses;

import com.example.myflower.dto.account.responses.AccountResponseDTO;
import lombok.*;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddressResponseDTO {
    private Integer id;
    private String recipientName;
    private String streetAddress;
    private String city;
    private String district;
    private String province;
    private String phoneNumber;
    private AccountResponseDTO account;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}