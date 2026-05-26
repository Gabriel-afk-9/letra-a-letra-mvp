package com.letraaletra.api.domain.game.board.cell.effect;

import com.letraaletra.api.domain.game.event.Event;

public record InteractResult(
        boolean canContinue,
        Event event
) {
}
