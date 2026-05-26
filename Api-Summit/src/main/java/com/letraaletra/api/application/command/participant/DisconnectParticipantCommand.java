package com.letraaletra.api.application.command.participant;

public record DisconnectParticipantCommand(
        String user,
        String session
) {
}
