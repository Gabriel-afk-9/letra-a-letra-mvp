package com.letraaletra.api.domain.game.event;

import com.letraaletra.api.domain.game.board.position.Position;

public record WordFoundedEvent(
        Position[] cells,
        String foundedBy
) implements EventData {
}
