package com.letraaletra.api.domain.game.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class GameIsRunningException extends DomainException {
    public GameIsRunningException() {
        super(GameMessages.GAME_IS_RUNNING);
    }
}
