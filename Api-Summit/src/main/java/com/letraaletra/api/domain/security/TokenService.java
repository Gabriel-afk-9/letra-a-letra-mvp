package com.letraaletra.api.domain.security;

public interface TokenService {
    String generateToken(String id);
    String getTokenContent(String token);
}
