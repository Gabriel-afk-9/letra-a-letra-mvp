package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;

@JsonTypeName("POWER_DISCARDED")
public record DiscardPowerResponseDTO(
        GameStateDTO data
) implements WsResponseDTO {
}
