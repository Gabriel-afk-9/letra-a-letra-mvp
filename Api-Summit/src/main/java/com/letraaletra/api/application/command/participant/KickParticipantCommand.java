package com.letraaletra.api.application.command.participant;

public record KickParticipantCommand(
        String token,
        String target,
        String user
) {
}
