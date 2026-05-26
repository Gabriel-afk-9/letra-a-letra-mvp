package com.letraaletra.api.domain.game.event;

public record PlayerBlindedEvent(
        String playerBlinded
) implements EventData {
}
