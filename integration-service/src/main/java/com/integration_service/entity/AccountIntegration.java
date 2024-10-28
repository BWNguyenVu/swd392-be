package com.integration_service.entity;

import com.integration_service.entity.enumType.AccountProviderEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Audited
@Entity
@Data
@NoArgsConstructor
public class AccountIntegration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private Boolean newVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountProviderEnum provider;


    @CreatedDate
    @Column(nullable = true, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = true)
    private LocalDateTime updatedAt;


}
