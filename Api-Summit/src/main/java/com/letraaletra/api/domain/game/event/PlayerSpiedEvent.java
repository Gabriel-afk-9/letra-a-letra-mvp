package com.letraaletra.api.domain.game.event;

public record PlayerSpiedEvent(
        String spiedBy
) implements EventData {
}
