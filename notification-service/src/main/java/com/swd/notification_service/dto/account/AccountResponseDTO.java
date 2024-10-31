package com.swd.notification_service.dto.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    @Column(nullable = true, length = 1000)
    private String avatar;
    private BigDecimal balance;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
