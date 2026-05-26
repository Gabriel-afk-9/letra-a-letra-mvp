package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.participant.exception.OnlyHostCanModerateException;

public class BanParticipantActorCommand implements ActorCommand<Game> {
    private final String user;
    private final String host;

    public BanParticipantActorCommand(String user, String host) {
        this.user = user;
        this.host = host;
    }

    @Override
    public Game execute(Game game) {
        if (!game.getHostId().equals(host)) {
            throw new OnlyHostCanModerateException();
        }

        game.addToBlackList(user);
        game.remove(user);

        return game;
    }
}
