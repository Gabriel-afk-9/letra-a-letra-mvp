package com.letraaletra.api.presentation.dto.request.game;

import jakarta.validation.constraints.NotNull;

public record RoomSettingsDTO(
        @NotNull
        boolean allowSpectators,

        @NotNull
        boolean privateGame
) {
}
