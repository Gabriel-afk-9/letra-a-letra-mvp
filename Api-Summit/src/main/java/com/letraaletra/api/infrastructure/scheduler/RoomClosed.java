package com.letraaletra.api.infrastructure.scheduler;

import com.letraaletra.api.domain.game.RoomCloseReasons;

public record RoomClosed(
        String event,
        RoomCloseReasons reason
) {
}
