package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.domain.game.event.Event;
import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;

import java.time.Instant;
import java.util.List;

@JsonTypeName("PLAYER_ACTION_RESULT")
public record PlayerActionResponseDTO(
        Instant turnEndsAt,
        List<Event> events,
        GameStateDTO data
) implements WsResponseDTO {
}
