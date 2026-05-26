package com.letraaletra.api.application.output.game;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.service.GameOverResult;

public record LeftGameOutput(
        String token,
        Game game,
        GameOverResult gameOverResult
) {
}
