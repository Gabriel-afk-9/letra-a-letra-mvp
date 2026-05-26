package com.letraaletra.api.presentation.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDTO(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8)
        @Size(max = 16)
        String password
) {
}
