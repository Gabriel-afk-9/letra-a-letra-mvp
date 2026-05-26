package com.letraaletra.api.application.command.player;

public record DiscardPowerCommand(
        String tokenGameId,
        String userId,
        String powerId
) {
}
