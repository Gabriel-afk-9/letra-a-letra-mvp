package com.letraaletra.api.application.command.game;

public record LeftGameCommand(
        String token,
        String session
) {
}
