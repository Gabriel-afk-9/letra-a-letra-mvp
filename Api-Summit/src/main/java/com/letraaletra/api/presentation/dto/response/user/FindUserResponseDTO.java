package com.letraaletra.api.presentation.dto.response.user;

public record FindUserResponseDTO(
        String id,
        String nickname,
        String avatar,
        String email
) {
}
