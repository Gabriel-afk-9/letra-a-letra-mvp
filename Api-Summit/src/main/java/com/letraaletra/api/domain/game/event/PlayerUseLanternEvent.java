package com.letraaletra.api.domain.game.event;

public record PlayerUseLanternEvent(
        String playerUseLantern
) implements EventData {
}
