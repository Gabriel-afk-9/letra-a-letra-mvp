package com.letraaletra.api.domain.user.exceptions;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.user.UserMessages;

public class EmailAlreadyInUseException extends DomainException {
    public EmailAlreadyInUseException() {
        super(UserMessages.EMAIL_ALREADY_IN_USE);
    }
}
