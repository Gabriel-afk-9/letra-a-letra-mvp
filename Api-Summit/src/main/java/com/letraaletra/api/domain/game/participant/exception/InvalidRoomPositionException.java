package com.letraaletra.api.domain.game.participant.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.user.UserMessages;

public class InvalidRoomPositionException extends DomainException {
    public InvalidRoomPositionException() {
        super(UserMessages.INVALID_ROOM_POSITION);
    }
}
