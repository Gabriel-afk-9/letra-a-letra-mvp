package com.letraaletra.api.domain.game.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class InsufficientPlayersException extends DomainException {
    public InsufficientPlayersException() {
        super(GameMessages.INSUFFICIENT_PLAYERS);
    }
}
