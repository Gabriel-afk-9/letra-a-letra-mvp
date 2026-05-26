package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.application.output.actor.LeftGameResult;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.exception.UserNotInGameException;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.service.GameOverResult;

public class LeftGameActorCommand implements ActorCommand<LeftGameResult> {
    private final String session;

    public LeftGameActorCommand(String session) {
        this.session = session;
    }

    @Override
    public LeftGameResult execute(Game game) {
        Participant participant = game.findBySession(session);
        validateParticipant(participant);

        game.remove(participant.getUserId());

        if (game.getGameStatus().equals(GameStatus.WAITING)) {
            return new LeftGameResult(
                    game,
                    participant.getUserId(),
                    game.getParticipants().isEmpty(),
                    null
            );
        }

        GameOverResult gameOverResult = game.getGameState().gameOverChecker();

        return new LeftGameResult(
                game,
                participant.getUserId(),
                game.getParticipants().isEmpty(),
                gameOverResult
        );
    }

    private void validateParticipant(Participant participant) {
        if (participant == null) {
            throw new UserNotInGameException();
        }
    }
}
