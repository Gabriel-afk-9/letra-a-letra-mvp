package com.letraaletra.api.application.output.user;

public record FindUserOutput(
        String id,
        String nickname,
        String avatar,
        String email
) {
}
