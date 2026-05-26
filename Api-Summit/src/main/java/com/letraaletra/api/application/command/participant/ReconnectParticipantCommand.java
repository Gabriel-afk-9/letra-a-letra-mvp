package com.letraaletra.api.application.command.participant;

public record ReconnectParticipantCommand(
        String user,
        String session
) {
}
