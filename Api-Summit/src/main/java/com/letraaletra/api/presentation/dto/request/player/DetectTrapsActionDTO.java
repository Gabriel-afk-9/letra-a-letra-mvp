package com.letraaletra.api.presentation.dto.request.player;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("DETECT_TRAPS")
public record DetectTrapsActionDTO(
        @NotBlank
        String actionId
) implements PlayerActionDTO {
}
