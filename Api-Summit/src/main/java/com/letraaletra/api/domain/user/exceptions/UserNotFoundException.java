package com.letraaletra.api.domain.user.exceptions;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.user.UserMessages;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException() {
        super(UserMessages.USER_NOT_FOUND);
    }
}
