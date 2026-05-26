package com.letraaletra.api.application.port;

import com.letraaletra.api.domain.game.Game;

public interface TurnTimeoutManager {
    void start(Game game);
}
