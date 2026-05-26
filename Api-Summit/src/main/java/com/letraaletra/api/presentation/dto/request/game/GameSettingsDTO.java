package com.letraaletra.api.presentation.dto.request.game;

import com.letraaletra.api.domain.game.state.GameMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GameSettingsDTO(
        @NotBlank
        String themeId,

        @NotNull
        GameMode gameMode
) {
}
