package com.example.myflower.service.impl;

import com.example.myflower.dto.jwt.requests.GenerateAccessTokenRequestDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.exception.token.InvalidToken;
import com.example.myflower.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Claims;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.exceptions.TokenExpiredException;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
@Component
public class JWTServiceImpl implements JWTService {
    @Value("${jwt.secret}")
    private String secret;

    private final long EXPIRATION = 1 * 24 * 60 * 60 * 1000;
    private final long EXPIRATION_REFRESHTOKEN = 7 * 24 * 60 * 60 * 1000;
    private final long EXPIRATION_OTP = 5 * 60 * 1000;
    @Override
    public String generateToken(String email) {
        Date now = new Date(); // get current time
        Date expirationDate = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    @Override
    public String generateAccessToken(GenerateAccessTokenRequestDTO requestDTO) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .setSubject(requestDTO.getEmail())
                .claim("userId", requestDTO.getUserId())
                .claim("role", requestDTO.getRole().toString())
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    @Override
    public String generateRefreshToken(String email) {
        Date now = new Date(); // get current time
        Date expirationDate = new Date(now.getTime() + EXPIRATION_REFRESHTOKEN);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    @Override
    public String generateTokenByOtp(String otp) {
        Date now = new Date(); // get current time
        Date expirationDate = new Date(now.getTime() + EXPIRATION_OTP);

        return Jwts.builder()
                .setSubject(otp)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    @Override
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            Instant expiredOn = e.getClaims().getExpiration().toInstant();
            throw new TokenExpiredException("Token has expired", expiredOn);
        } catch (JwtException e) {
            throw new InvalidToken("Invalid token");
        } catch (Exception e) {
            throw new InvalidToken("Error parsing token: " + e.getMessage());
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public Boolean validateToken(String token, Account userDetails) {
        final String userName = extractEmail(token);
        return (userName.equals(userDetails.getEmail()) && !isTokenExpired(token));
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> getPayload(String token) throws IOException {
        DecodedJWT decodedJWT = JWT.decode(token);
        String payload = new String(java.util.Base64.getUrlDecoder().decode(decodedJWT.getPayload()));
        return objectMapper.readValue(payload, Map.class);
    }

    @Override
    public boolean verifyToken(String token, boolean isRefresh) {
        try {
            Claims claims = extractAllClaims(token);

            Date expiryTime = isRefresh
                    ? new Date(claims.getIssuedAt().toInstant().plusMillis(EXPIRATION_REFRESHTOKEN).toEpochMilli())
                    : claims.getExpiration();

            if (expiryTime.after(new Date()) && !isTokenExpired(token)) {
                return true;
            }
        } catch (JwtException | InvalidToken e) {
            return false;
        }

        return false;
    }
}
