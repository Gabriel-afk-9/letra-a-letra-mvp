package com.letraaletra.api.domain.security;

public interface PasswordService {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String hashedPassword);
}
