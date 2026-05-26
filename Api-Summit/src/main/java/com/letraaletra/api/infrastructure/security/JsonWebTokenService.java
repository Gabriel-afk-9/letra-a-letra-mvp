package com.letraaletra.api.infrastructure.security;

import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.security.exceptions.InvalidTokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JsonWebTokenService implements TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String id) {
        return Jwts.builder()
                .claim("id", id)
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + (6 * 60 * 60 * 1000L)))
                .signWith(getSigningKey())
                .compact();
    }

    public String getTokenContent(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("id", String.class);

        } catch (Exception ex) {
            throw new InvalidTokenException();
        }
    }
}
