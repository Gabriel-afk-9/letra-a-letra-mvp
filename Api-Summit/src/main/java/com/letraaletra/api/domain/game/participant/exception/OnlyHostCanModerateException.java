package com.letraaletra.api.domain.game.participant.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class OnlyHostCanModerateException extends DomainException {
    public OnlyHostCanModerateException() {
        super(GameMessages.ONLY_HOST_CAN_MODERATE);
    }
}
