package com.letraaletra.api.presentation.dto.request.player;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("LANTERN")
public record LanternActionDTO(
        @NotBlank
        String actionId
) implements PlayerActionDTO {
}
