package com.letraaletra.api.application.output.actor;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.service.GameOverResult;

public record LeftGameResult(
        Game game,
        String user,
        boolean isEmpty,
        GameOverResult gameOverResult
) {}