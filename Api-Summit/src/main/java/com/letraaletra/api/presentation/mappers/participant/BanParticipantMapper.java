package com.letraaletra.api.presentation.mappers.participant;

import com.letraaletra.api.application.command.participant.BanParticipantCommand;
import com.letraaletra.api.application.output.participant.BanParticipantOutput;
import com.letraaletra.api.presentation.dto.request.BanParticipantWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.BanParticipantResponseDTO;
import com.letraaletra.api.presentation.mappers.game.GameDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BanParticipantMapper {
    @Autowired
    private GameDTOMapper gameDTOMapper;


    public BanParticipantCommand toCommand(BanParticipantWsRequest request, String user) {
        return new BanParticipantCommand(
                request.tokenGameId(),
                request.participantId(),
                user
        );
    }

    public BanParticipantResponseDTO toResponseDTO(BanParticipantOutput output) {
        return new BanParticipantResponseDTO(
               gameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
