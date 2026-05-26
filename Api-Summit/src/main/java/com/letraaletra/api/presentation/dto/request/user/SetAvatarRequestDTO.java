package com.letraaletra.api.presentation.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public record SetAvatarRequestDTO(
        @NotBlank
        String avatar
) {
}
