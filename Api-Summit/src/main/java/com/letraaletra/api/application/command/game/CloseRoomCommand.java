package com.letraaletra.api.application.command.game;

import com.letraaletra.api.domain.game.Game;

public record CloseRoomCommand(
        Game game
) {
}
