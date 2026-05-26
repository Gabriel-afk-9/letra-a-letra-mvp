package com.letraaletra.api.domain.game.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class GameNotRunningException extends DomainException {
    public GameNotRunningException() {
        super(GameMessages.GAME_NOT_RUNNING);
    }
}
