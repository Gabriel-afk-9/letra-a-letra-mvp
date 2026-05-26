package com.letraaletra.api.domain.game.player.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.game.player.PlayerMessages;

public class PlayerNotInGameException extends DomainException {
    public PlayerNotInGameException() {
        super(PlayerMessages.PLAYER_NOT_IN_GAME);
    }
}
