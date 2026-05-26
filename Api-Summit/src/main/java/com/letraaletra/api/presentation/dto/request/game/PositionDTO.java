package com.letraaletra.api.presentation.dto.request.game;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PositionDTO(
        @NotNull
        @Min(0)
        @Max(9)
        Integer x,

        @NotNull
        @Min(0)
        @Max(9)
        Integer y
) {
}
