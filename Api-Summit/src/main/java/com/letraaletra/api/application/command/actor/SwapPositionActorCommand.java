package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.exception.UserNotInGameException;
import com.letraaletra.api.domain.game.participant.Participant;

public class SwapPositionActorCommand implements ActorCommand<Game> {
    private final String user;
    private final int position;

    public SwapPositionActorCommand(String user, int position) {
        this.user = user;
        this.position = position;
    }

    @Override
    public Game execute(Game game) {
        Participant participant = game.getParticipantByUserId(user);

        validateParticipant(participant);

        game.changePosition(user, position);

        return game;
    }

    private void validateParticipant(Participant participant) {
        if (participant == null) {
            throw new UserNotInGameException();
        }
    }
}
