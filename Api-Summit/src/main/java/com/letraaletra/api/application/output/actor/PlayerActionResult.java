package com.letraaletra.api.application.output.actor;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.event.Event;
import com.letraaletra.api.domain.game.event.StateEvent;
import com.letraaletra.api.domain.game.service.GameOverResult;

import java.util.List;

public record PlayerActionResult(
        List<Event> events,
        GameOverResult gameOverResult,
        Game game
) {
}
