package com.letraaletra.api.domain.game.event;

import com.letraaletra.api.domain.game.board.position.Position;

public record CellStillBlockedEvent(
        Position cell,
        String blockedBy,
        int remainingAttempts
) implements EventData {
}
