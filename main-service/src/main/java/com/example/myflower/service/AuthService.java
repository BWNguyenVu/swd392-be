package com.example.myflower.service;

import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.auth.requests.ChangeEmailRequestDTO;
import com.example.myflower.dto.auth.requests.IntrospectRequestDTO;
import com.example.myflower.dto.auth.responses.AuthResponseDTO;
import com.example.myflower.dto.auth.responses.IntrospectResponseDTO;

public interface AuthService {
    AuthResponseDTO renewAccessToken(String token);
    AccountResponseDTO changeEmail(ChangeEmailRequestDTO changeEmailRequestDTO);
    AccountResponseDTO confirmChangeEmail(String otp);
    IntrospectResponseDTO introspect(IntrospectRequestDTO request);
}
