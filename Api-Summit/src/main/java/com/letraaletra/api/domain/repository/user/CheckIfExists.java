package com.letraaletra.api.domain.repository.user;

public interface CheckIfExists {
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
}
