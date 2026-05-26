package com.letraaletra.api.application.command.participant;

public record UnbanParticipantCommand(
        String token,
        String target,
        String user
) {
}
