package com.letraaletra.api.presentation.dto.request.player;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.request.game.PositionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonTypeName("UNBLOCK")
public record UnblockActionDTO(
        @NotBlank
        String actionId,

        @Valid
        @NotNull
        PositionDTO position
) implements PlayerActionDTO {
}
