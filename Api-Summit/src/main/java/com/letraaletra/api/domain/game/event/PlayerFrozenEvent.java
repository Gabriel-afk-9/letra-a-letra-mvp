package com.letraaletra.api.domain.game.event;

public record PlayerFrozenEvent(
        String playerFrozen
) implements EventData {
}
