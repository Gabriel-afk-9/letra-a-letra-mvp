package com.letraaletra.api.application.output.game;

import com.letraaletra.api.domain.game.Game;

public record CreateGameOutput(
        String token,
        Game game
) {
}
