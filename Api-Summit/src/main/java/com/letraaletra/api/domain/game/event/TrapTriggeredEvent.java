package com.letraaletra.api.domain.game.event;

import com.letraaletra.api.domain.game.board.position.Position;

public record TrapTriggeredEvent(
        Position cell,
        String triggeredBy
) implements EventData {
}
