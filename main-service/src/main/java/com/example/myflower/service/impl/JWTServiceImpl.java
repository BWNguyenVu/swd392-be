package com.example.myflower.service.impl;

import com.example.myflower.entity.Account;
import com.example.myflower.exception.token.InvalidToken;
import com.example.myflower.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final String SECRET = "CRAZY@IWASC";
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
                .signWith(SignatureAlgorithm.HS512, SECRET)
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
                .signWith(SignatureAlgorithm.HS256, SECRET)
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
                .signWith(SignatureAlgorithm.HS512, SECRET)
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
                    .setSigningKey(SECRET)
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
}
