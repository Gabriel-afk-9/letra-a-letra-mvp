package com.letraaletra.api.application.command.game;

public record ExpireTurnCommand(
        String gameId,
        int version
) {
}
