package com.letraaletra.api.domain.repository.user;

import com.letraaletra.api.domain.user.User;

import java.util.Optional;

public interface FindUser {
    Optional<User> find(String id);
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleId(String googleId);
}
