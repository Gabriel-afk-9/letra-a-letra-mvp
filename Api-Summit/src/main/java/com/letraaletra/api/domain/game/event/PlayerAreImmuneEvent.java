package com.letraaletra.api.domain.game.event;

public record PlayerAreImmuneEvent(
        String playerImmune
) implements EventData {
}
