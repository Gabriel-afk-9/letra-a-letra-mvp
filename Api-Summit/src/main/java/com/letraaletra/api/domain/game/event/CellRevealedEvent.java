package com.letraaletra.api.domain.game.event;

import com.letraaletra.api.domain.game.board.position.Position;

public record CellRevealedEvent(
        Position cell,
        String revealedBy
) implements EventData {
}
