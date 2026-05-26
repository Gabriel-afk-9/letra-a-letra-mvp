package com.letraaletra.api.application.context;

import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.exception.*;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.exception.InvalidModerateActionException;
import com.letraaletra.api.domain.game.participant.exception.OnlyHostCanModerateException;
import com.letraaletra.api.domain.security.TokenService;

public class ModerationContextFactory {
    private final ActorManager<Game> actorManager;
    private final TokenService tokenService;

    public ModerationContextFactory(
            ActorManager<Game> actorManager,
            TokenService tokenService
    ) {
        this.actorManager = actorManager;
        this.tokenService = tokenService;
    }

    public ModerationContext resolve(String tokenGameId, String targetId, String hostId) {
        String gameId = tokenService.getTokenContent(tokenGameId);

        Game game = actorManager.get(gameId).getGame();

        validateUser(hostId, game);
        validateAction(targetId, hostId);

        Participant participant = game.getParticipantByUserId(targetId);

        validateParticipant(participant);

        return new ModerationContext(
                game,
                participant
        );
    }

    private void validateAction(String targetId, String hostId) {
        if (targetId.equals(hostId)) {
            throw new InvalidModerateActionException();
        }
    }

    private void validateParticipant(Participant participant) {
        if (participant == null) {
            throw new UserNotInGameException();
        }
    }

    private void validateUser(String userId, Game game) {
        if (!game.getHostId().equals(userId)) {
            throw new OnlyHostCanModerateException();
        }
    }
}
