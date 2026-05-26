package com.letraaletra.api.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.request.player.PlayerActionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonTypeName("PLAYER_ACTION")
public record PlayerActionWsRequest(
        @NotBlank
        String tokenGameId,

        @Valid
        @NotNull
        PlayerActionDTO action
) implements WsRequestDTO {
}
