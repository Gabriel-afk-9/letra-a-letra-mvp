package com.letraaletra.api.application.output.actor;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.service.GameOverResult;

public record ExpireTurnResult(
        String whoPassed,
        Game game,
        GameOverResult gameOverResult,
        boolean removedBecauseAfk
) {
}
