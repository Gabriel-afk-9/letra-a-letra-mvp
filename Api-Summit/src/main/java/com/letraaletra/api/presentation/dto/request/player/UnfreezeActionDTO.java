package com.letraaletra.api.presentation.dto.request.player;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("UNFREEZE")
public record UnfreezeActionDTO(
        @NotBlank
        String actionId
) implements PlayerActionDTO {
}
