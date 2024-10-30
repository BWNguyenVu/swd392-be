package com.swd.notification_service.dto.account;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO implements Serializable {
    private Integer id;
    private String name;
    private String email;
}
