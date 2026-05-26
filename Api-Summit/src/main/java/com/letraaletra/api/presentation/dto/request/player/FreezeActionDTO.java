package com.letraaletra.api.presentation.dto.request.player;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("FREEZE")
public record FreezeActionDTO(
        @NotBlank
        String actionId,

        @NotBlank
        String targetId
) implements PlayerActionDTO {
}
