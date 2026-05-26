package com.letraaletra.api.presentation.dto.request.player;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.request.game.PositionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@JsonTypeName("REVEAL")
public record RevealActionDTO(
        @Valid
        @NotNull
        PositionDTO position
) implements PlayerActionDTO {
}
