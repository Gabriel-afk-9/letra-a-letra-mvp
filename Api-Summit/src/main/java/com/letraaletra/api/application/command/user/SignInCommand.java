package com.letraaletra.api.application.command.user;

public record SignInCommand(
        String email,
        String password
) {
}
