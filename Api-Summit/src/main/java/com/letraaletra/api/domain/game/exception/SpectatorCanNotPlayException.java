package com.letraaletra.api.domain.game.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class SpectatorCanNotPlayException extends DomainException {
    public SpectatorCanNotPlayException() {
        super(GameMessages.SPECTATOR_CAN_NOT_PLAY);
    }
}
