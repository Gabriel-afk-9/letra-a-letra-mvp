package com.letraaletra.api.application.command.participant;

public record SwapPositionCommand(
        String token,
        Integer position,
        String user
) {}
