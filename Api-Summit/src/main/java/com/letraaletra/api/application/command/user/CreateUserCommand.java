package com.letraaletra.api.application.command.user;

public record CreateUserCommand(
        String email,
        String password
) {
}
