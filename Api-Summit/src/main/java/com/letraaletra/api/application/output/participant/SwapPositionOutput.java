package com.letraaletra.api.application.output.participant;

import com.letraaletra.api.domain.game.Game;

public record SwapPositionOutput(
        String token,
        Game game
) {
}
