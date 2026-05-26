package com.letraaletra.api.application.output.game;

import com.letraaletra.api.domain.game.Game;

public record JoinGameOutput(
        String token,
        Game game
) {
}
