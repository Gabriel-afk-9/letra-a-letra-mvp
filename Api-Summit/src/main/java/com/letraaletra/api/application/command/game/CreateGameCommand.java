package com.letraaletra.api.application.command.game;

import com.letraaletra.api.domain.game.RoomSettings;

public record CreateGameCommand(
        String name,
        RoomSettings settings,
        String session,
        String user
) {
}
