package com.letraaletra.api.domain.game.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class GameNotFoundException extends DomainException {
    public GameNotFoundException() {
        super(GameMessages.GAME_NOT_FOUND);
    }
}
