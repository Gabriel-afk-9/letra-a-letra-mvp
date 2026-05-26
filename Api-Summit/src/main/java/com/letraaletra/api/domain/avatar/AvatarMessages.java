package com.letraaletra.api.domain.avatar;

import com.letraaletra.api.domain.MessageCode;

public enum AvatarMessages implements MessageCode {
    FAILED_TO_LOAD_AVATARS("failed_to_load_avatars"),
    AVATAR_NOT_FOUND("avatar_not_found");

    private final String message;

    AvatarMessages(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
