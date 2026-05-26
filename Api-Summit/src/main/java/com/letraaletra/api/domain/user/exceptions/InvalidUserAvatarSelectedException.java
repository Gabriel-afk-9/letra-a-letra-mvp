package com.letraaletra.api.domain.user.exceptions;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.user.UserMessages;

public class InvalidUserAvatarSelectedException extends DomainException {
    public InvalidUserAvatarSelectedException() {
        super(UserMessages.INVALID_AVATAR);
    }
}
