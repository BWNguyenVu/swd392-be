package com.example.myflower.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.myflower.entity.Account;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public interface JWTService {
    String generateToken(String email);

    String generateRefreshToken(String email);

    String extractEmail(String token);

    String extractSubject(String token);

    Date extractExpiration(String token);

    <T> T extractClaim(String token, java.util.function.Function<io.jsonwebtoken.Claims, T> claimResolver);

    io.jsonwebtoken.Claims extractAllClaims(String token) throws TokenExpiredException;

    Boolean validateToken(String token, Account userDetails);

    Map<String, Object> getPayload(String token) throws IOException;

    String generateTokenByOtp(String otp);
}
