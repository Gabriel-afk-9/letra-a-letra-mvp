package com.letraaletra.api.application.command.user;

public record SetNicknameCommand(
        String user,
        String nickname
) {
}
