package com.letraaletra.api.domain.game.participant.factory;

import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.user.User;

public class ParticipantFactory {
    public static Participant fromUser(User user, String sessionId) {
        return new Participant(
                user.getId(),
                sessionId,
                user.getNickname(),
                user.getAvatar()
        );
    }
}
