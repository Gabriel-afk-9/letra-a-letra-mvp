package com.letraaletra.api.domain.security.exceptions;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.user.UserMessages;

public class InvalidPasswordException extends DomainException {
    public InvalidPasswordException() {
        super(UserMessages.INVALID_CREDENTIALS);
    }
}
