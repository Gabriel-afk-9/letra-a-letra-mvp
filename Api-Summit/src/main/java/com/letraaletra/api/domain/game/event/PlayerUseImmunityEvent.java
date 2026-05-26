package com.letraaletra.api.domain.game.event;

public record PlayerUseImmunityEvent(
        String playerUseImmunity
) implements EventData {
}
