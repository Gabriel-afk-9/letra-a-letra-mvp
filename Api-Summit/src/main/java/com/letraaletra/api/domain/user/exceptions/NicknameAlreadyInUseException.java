package com.letraaletra.api.domain.user.exceptions;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.user.UserMessages;

public class NicknameAlreadyInUseException extends DomainException {
    public NicknameAlreadyInUseException() {
        super(UserMessages.NICKNAME_ALREADY_IN_USE);
    }
}
