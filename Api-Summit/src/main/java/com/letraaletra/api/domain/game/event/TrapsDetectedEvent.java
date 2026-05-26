package com.letraaletra.api.domain.game.event;

public record TrapsDetectedEvent(
        String detectedBy
) implements EventData {
}
