package com.letraaletra.api.domain.game.event;

public record TurnPassedEvent(
        String passedBy
) implements EventData {
}
