package com.letraaletra.api.domain.game.event;

public record Event(
        StateEvent event,
        EventData data
) {
}
