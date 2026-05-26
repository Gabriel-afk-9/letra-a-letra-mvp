package com.letraaletra.api.domain.game.event;

public record PlayerUnfreezeEvent(
        String playerUnfreeze
) implements EventData {
}
