package com.letraaletra.api.presentation.mappers.participant;

import com.letraaletra.api.application.command.participant.ReconnectParticipantCommand;
import com.letraaletra.api.application.output.participant.ReconnectParticipantOutput;
import com.letraaletra.api.presentation.dto.response.websocket.ReconnectParticipantResponseDTO;
import com.letraaletra.api.presentation.mappers.game.GameStateDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReconnectParticipantMapper {
    @Autowired
    private GameStateDTOMapper gameStateDTOMapper;

    public ReconnectParticipantCommand toCommand(String user, String session) {
        return new ReconnectParticipantCommand(
                user,
                session
        );
    }

    public ReconnectParticipantResponseDTO toResponseDTO(ReconnectParticipantOutput output) {
        return new ReconnectParticipantResponseDTO(
                gameStateDTOMapper.toAllDTO(output.game())
        );
    }
}
