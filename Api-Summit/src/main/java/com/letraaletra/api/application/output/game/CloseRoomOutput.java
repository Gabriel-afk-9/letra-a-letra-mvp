package com.letraaletra.api.application.output.game;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.RoomCloseReasons;

public record CloseRoomOutput(
        Game game,
        String event,
        RoomCloseReasons reason
) {
}
