package com.example.myflower.service;

import com.example.myflower.entity.Account;
import com.example.myflower.exception.token.InvalidToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
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
public class JWTService {
    private static final String SECRET = "CRAZY@IWASCRAZYONCE@THEYPUTMEINARUBBERROOM@ARUBBERROOMWITHRATS@ANDRATSMAKEMECRAZY!@$";
    private final long EXPIRATION = 1 * 24 * 60 * 60 * 1000;
    private final long EXPIRATION_REFRESHTOKEN = 7 * 24 * 60 * 60 * 1000;
    public String generateToken(String email) {
        Date now = new Date(); // get current time
        Date expirationDate = new Date(now.getTime() + EXPIRATION);

        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        return token;
    }

    public String generateRefreshToken(String email) {
        Date now = new Date(); // get current time
        Date expirationDate = new Date(now.getTime() + EXPIRATION_REFRESHTOKEN);

        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
        return token;
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            Instant expiredOn = e.getClaims().getExpiration().toInstant();
            throw new TokenExpiredException("Token has expired", expiredOn);
        }  catch (JwtException e) {
            throw new InvalidToken("Invalid token");
        } catch (Exception e) {
            throw new InvalidToken("Error parsing token: " + e.getMessage());
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before((new Date()));
    }

    public Boolean validateToken(String token, Account userDetails){
        final String userName= extractEmail(token);
        return (userName.equals(userDetails.getEmail()) && !isTokenExpired(token));
    }


    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> getPayload(String token) throws IOException {
        DecodedJWT decodedJWT = JWT.decode(token);
        String payload = new String(java.util.Base64.getUrlDecoder().decode(decodedJWT.getPayload()));
        return objectMapper.readValue(payload, Map.class);
    }
}
