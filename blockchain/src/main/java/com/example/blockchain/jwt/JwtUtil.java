package com.example.blockchain.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperties jwtProperties;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.getSecret()));
    }

    public String generateAccessToken(String username, String address, String privateKey, String role) {
        return generateToken(username, address, privateKey, role, jwtProperties.getAccessTokenExpiration());
    }

    public String generateRefreshToken(String username, String address, String privateKey, String role) {
        return generateToken(username, address, privateKey, role, jwtProperties.getRefreshTokenExpiration());
    }

    private String generateToken(String username, String address, String privateKey, String role, long expireTime) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("address", address)
                .claim("privateKey", privateKey)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public String getAddress(String token) {
        return parseClaims(token).get("address", String.class);
    }

    public String getPrivateKey(String token) {
        return parseClaims(token).get("privateKey", String.class);
    }


    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println(" 토큰 만료: " + e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println(" 잘못된 토큰: " + e.getMessage());
        }
        return false;
    }

    public long getRemainingMillis(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }


    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
