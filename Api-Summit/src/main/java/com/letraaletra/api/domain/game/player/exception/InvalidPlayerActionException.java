package com.letraaletra.api.domain.game.player.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.game.player.PlayerMessages;

public class InvalidPlayerActionException extends DomainException {
    public InvalidPlayerActionException() {
        super(PlayerMessages.INVALID_PLAYER_ACTION);
    }
}
