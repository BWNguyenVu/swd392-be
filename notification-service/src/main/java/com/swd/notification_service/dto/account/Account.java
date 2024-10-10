package com.swd.notification_service.dto.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Getter
@Setter
public class Account implements Serializable {
    private String name;
    private String email;
    private String tokens;
    private String otp;
}
