package com.letraaletra.api.application.command.game;

public record JoinGameCommand(
        String token,
        String session,
        String user
) {
}
