package com.letraaletra.api.application.output.user;

public record CreateUserOutput(
        String id,
        String nickname,
        String avatar,
        String email
) {
}
