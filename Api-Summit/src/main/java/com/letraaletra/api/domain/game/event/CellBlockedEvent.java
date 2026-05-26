package com.letraaletra.api.domain.game.event;

import com.letraaletra.api.domain.game.board.position.Position;

public record CellBlockedEvent(
        Position cell,
        String blockedBy
) implements EventData {
}
