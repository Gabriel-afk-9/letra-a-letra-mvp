package com.letraaletra.api.presentation.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthRequestDTO(
        @NotBlank
        String token
) {
}
