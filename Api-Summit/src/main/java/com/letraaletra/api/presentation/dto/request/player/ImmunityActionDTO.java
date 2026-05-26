package com.letraaletra.api.presentation.dto.request.player;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("IMMUNITY")
public record ImmunityActionDTO(
        @NotBlank
        String actionId
) implements PlayerActionDTO {
}
