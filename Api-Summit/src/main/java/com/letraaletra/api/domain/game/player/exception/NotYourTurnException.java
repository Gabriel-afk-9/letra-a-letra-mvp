package com.letraaletra.api.domain.game.player.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class NotYourTurnException extends DomainException {
    public NotYourTurnException() {
        super(GameMessages.NOT_YOUR_TURN);
    }
}
