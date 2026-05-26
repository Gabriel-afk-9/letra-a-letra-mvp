package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.exception.UserBannedException;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.factory.ParticipantFactory;
import com.letraaletra.api.domain.user.User;

public class JoinGameActorCommand implements ActorCommand<Game> {
    private final User user;
    private final String session;

    public JoinGameActorCommand(User user, String session) {
        this.user = user;
        this.session = session;
    }

    @Override
    public Game execute(Game game) {
        checkIfBlackListed(game, user.getId());

        Participant participant = ParticipantFactory.fromUser(user, session);

        game.join(participant);
        user.enterGame(game.getId());

        return game;
    }

    private void checkIfBlackListed(Game game, String userId) {
        if (game.isBlackListed(userId)) {
            throw new UserBannedException();
        }
    }
}
