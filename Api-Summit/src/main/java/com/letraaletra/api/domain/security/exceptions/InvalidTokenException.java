package com.letraaletra.api.domain.security.exceptions;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.user.UserMessages;

public class InvalidTokenException extends DomainException {
    public InvalidTokenException() {
        super(UserMessages.INVALID_TOKEN);
    }
}
