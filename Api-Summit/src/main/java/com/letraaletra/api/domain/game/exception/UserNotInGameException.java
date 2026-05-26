package com.letraaletra.api.domain.game.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.user.UserMessages;

public class UserNotInGameException extends DomainException {
    public UserNotInGameException() {
        super(UserMessages.USER_NOT_IN_GAME);
    }
}
