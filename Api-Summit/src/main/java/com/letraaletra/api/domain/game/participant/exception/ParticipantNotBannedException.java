package com.letraaletra.api.domain.game.participant.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class ParticipantNotBannedException extends DomainException {
    public ParticipantNotBannedException() {
        super(GameMessages.PARTICIPANT_NOT_BANNED);
    }
}
