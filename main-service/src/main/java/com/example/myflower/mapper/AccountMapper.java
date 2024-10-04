package com.example.myflower.mapper;

import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.entity.Account;

public class AccountMapper {

    public static AccountResponseDTO mapToAccountResponseDTO(Account account) {
        return AccountResponseDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .phone(account.getPhone())
                .gender(account.getGender())
                .role(account.getRole())
                .externalAuthType(account.getExternalAuthType())
                .avatar(account.getAvatar())
                .balance(account.getBalance())
                .createAt(account.getCreateAt())
                .updateAt(account.getUpdateAt())
                .build();
    }
}
