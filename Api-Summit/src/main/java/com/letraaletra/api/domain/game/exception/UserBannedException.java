package com.letraaletra.api.domain.game.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.user.UserMessages;

public class UserBannedException extends DomainException {
    public UserBannedException() {
        super(UserMessages.USER_BANNED);
    }
}
