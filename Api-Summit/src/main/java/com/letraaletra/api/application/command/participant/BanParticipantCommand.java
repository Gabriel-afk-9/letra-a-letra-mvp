package com.letraaletra.api.application.command.participant;

public record BanParticipantCommand(
        String token,
        String target,
        String user
) {
}
