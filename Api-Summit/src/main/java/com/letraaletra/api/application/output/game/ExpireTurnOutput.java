package com.letraaletra.api.application.output.game;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.service.GameOverResult;

public record ExpireTurnOutput(
        String event,
        String user,
        String currentPlayerTurnId,
        Game game,
        GameOverResult gameOverResult,
        boolean removedBecauseAfk
) {
}
