package com.example.myflower.service;

import com.example.myflower.dto.auth.responses.AccountResponseDTO;

public interface AuthService {
    AccountResponseDTO renewAccessToken(String token);
}
