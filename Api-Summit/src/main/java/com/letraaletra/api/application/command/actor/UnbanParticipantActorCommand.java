package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.participant.exception.OnlyHostCanModerateException;

public class UnbanParticipantActorCommand implements ActorCommand<Game> {
    private final String user;
    private final String host;

    public UnbanParticipantActorCommand(String user, String host) {
        this.user = user;
        this.host = host;
    }

    @Override
    public Game execute(Game game) {
        if (!game.getHostId().equals(host)) {
            throw new OnlyHostCanModerateException();
        }

        game.removeFromBlackList(user);

        return game;
    }
}
