package com.letraaletra.api.application.output.player;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.event.Event;
import com.letraaletra.api.domain.game.event.StateEvent;
import com.letraaletra.api.domain.game.service.GameOverResult;

import java.util.List;
import java.util.Optional;

public record PlayerActionOutput(
        Game game,
        List<Event> events,
        Optional<GameOverResult> gameOver
) {
}
