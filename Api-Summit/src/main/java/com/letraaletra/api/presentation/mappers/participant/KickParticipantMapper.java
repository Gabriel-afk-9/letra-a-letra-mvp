package com.letraaletra.api.presentation.mappers.participant;

import com.letraaletra.api.application.command.participant.KickParticipantCommand;
import com.letraaletra.api.application.output.participant.KickParticipantOutput;
import com.letraaletra.api.presentation.dto.request.KickParticipantWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.KickParticipantResponseDTO;
import com.letraaletra.api.presentation.mappers.game.GameDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KickParticipantMapper {
    @Autowired
    private GameDTOMapper gameDTOMapper;

    public KickParticipantCommand toCommand(KickParticipantWsRequest request, String userId) {
        return new KickParticipantCommand(
                request.tokenGameId(),
                request.participantId(),
                userId
        );
    }

    public KickParticipantResponseDTO toResponseDTO(KickParticipantOutput output) {
        return new KickParticipantResponseDTO(
                gameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
