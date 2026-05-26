package com.letraaletra.api.presentation.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SetNicknameRequestDTO(
        @NotBlank
        @Size(min = 5, max = 10)
        String nickname
) {
}
