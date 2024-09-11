package com.example.myflower.entity;

import com.example.myflower.entity.enumType.AccountGenderEnum;
import com.example.myflower.entity.enumType.AccountProviderEnum;
import com.example.myflower.entity.enumType.AccountStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Account implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;
    @Column(nullable = true)
    private String phone;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private AccountGenderEnum gender;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountGenderEnum role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountProviderEnum provider;

    @Column(nullable = true)
    private String avatar;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = true)
    private LocalDateTime updateAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatusEnum status;

    @Transient
    private String tokens;

    @Transient
    private String refreshToken;

    @Transient
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(this.role.name()));
        return authorities;
    }
    @Transient
    @Override
    public String getUsername() {
        return this.email;
    }

    @Transient
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Transient
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Transient
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Transient
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

}
