package com.letraaletra.api.domain.game.participant.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class InvalidModerateActionException extends DomainException {
    public InvalidModerateActionException() {
        super(GameMessages.INVALID_MODERATE_ACTION);
    }
}
