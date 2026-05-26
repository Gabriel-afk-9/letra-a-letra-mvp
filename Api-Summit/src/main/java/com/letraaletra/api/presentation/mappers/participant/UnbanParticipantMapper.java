package com.letraaletra.api.presentation.mappers.participant;

import com.letraaletra.api.application.command.participant.UnbanParticipantCommand;
import com.letraaletra.api.application.output.participant.UnbanParticipantOutput;
import com.letraaletra.api.presentation.dto.request.UnbanParticipantWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.UnbanParticipantResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UnbanParticipantMapper {
    public UnbanParticipantCommand toCommand(UnbanParticipantWsRequest request, String userId) {
        return new UnbanParticipantCommand(
                request.tokenGameId(),
                request.userId(),
                userId
        );
    }

    public UnbanParticipantResponseDTO toResponseDTO(UnbanParticipantOutput output) {
        return new UnbanParticipantResponseDTO(

        );
    }
}
