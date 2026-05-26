package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.domain.game.Game;

public interface ActorCommand<T> {
    T execute(Game game);
}
