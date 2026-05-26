package com.letraaletra.api.presentation.mappers.participant;

import com.letraaletra.api.application.command.participant.DisconnectParticipantCommand;
import com.letraaletra.api.application.output.participant.DisconnectParticipantOutput;
import com.letraaletra.api.presentation.dto.response.websocket.DisconnectParticipantResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class DisconnectParticipantMapper {
    public DisconnectParticipantCommand toCommand(String user, String session) {
        return new DisconnectParticipantCommand(
                user,
                session
        );
    }

    public DisconnectParticipantResponseDTO toResponseDTO(DisconnectParticipantOutput output) {
        return new DisconnectParticipantResponseDTO(
            output.user()
        );
    }
}
