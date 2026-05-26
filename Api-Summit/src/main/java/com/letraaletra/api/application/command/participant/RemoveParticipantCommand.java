package com.letraaletra.api.application.command.participant;

public record RemoveParticipantCommand(
        String gameId,
        String userId
) {
}
