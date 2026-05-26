package com.letraaletra.api.infrastructure.scheduler;

public record TurnExpired(
        String event,
        ExpiredData data
) {
    public record ExpiredData(
            String user,
            String currentTurnPlayerId
    ) {}
}
