package com.letraaletra.api.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.request.game.RoomSettingsDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonTypeName("CREATE_GAME")
public record CreateGameWsRequest(
        @NotBlank
        String name,

        @Valid
        @NotNull
        RoomSettingsDTO settings
) implements WsRequestDTO {
}
