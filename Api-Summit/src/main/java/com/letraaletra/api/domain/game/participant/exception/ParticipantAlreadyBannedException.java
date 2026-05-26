package com.letraaletra.api.domain.game.participant.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class ParticipantAlreadyBannedException extends DomainException {
    public ParticipantAlreadyBannedException() {
        super(GameMessages.PARTICIPANT_ALREADY_BANNED);
    }
}
