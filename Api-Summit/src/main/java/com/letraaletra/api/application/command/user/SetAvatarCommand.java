package com.letraaletra.api.application.command.user;

public record SetAvatarCommand(
        String userId,
        String avatar
) {
}
