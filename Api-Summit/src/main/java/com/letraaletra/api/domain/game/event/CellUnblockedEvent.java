package com.letraaletra.api.domain.game.event;

import com.letraaletra.api.domain.game.board.position.Position;

public record CellUnblockedEvent(
        Position cell,
        String unblockedBy
) implements EventData {
}
