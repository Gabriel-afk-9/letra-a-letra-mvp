package com.letraaletra.api.domain.user.exceptions;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.user.UserMessages;

public class UserAlreadyInGameException extends DomainException {
    public UserAlreadyInGameException() {
        super(UserMessages.USER_ALREADY_IN_GAME);
    }
}
