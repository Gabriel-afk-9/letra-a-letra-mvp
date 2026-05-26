package com.letraaletra.api.domain.game.event;

import com.letraaletra.api.domain.game.board.position.Position;

public record CellTrappedEvent(
        Position cell,
        String trappedBy
) implements EventData {
}
