package com.letraaletra.api.domain.user.factory;

import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.stats.UserStats;

public class UserFactory {
    private static final String defaultAvatar = "old-man";

    public User createLocal(String id, String nickname, String email, String passwordHash) {
        return new User(
                id,
                nickname,
                defaultAvatar,
                email,
                passwordHash,
                null,
                true,
                getInitial()
        );
    }

    public User createGoogle(String id, String email, String googleId) {
        return new User(
                id,
                null,
                defaultAvatar,
                email,
                null,
                googleId,
                true,
                getInitial()
        );
    }

    private UserStats getInitial() {
        return new UserStats(0, 0, 0, 0);
    }
}
