package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.command.participant.BanParticipantCommand;
import com.letraaletra.api.application.output.participant.BanParticipantOutput;
import com.letraaletra.api.application.usecase.participant.BanParticipantUseCase;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.presentation.dto.request.BanParticipantWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.BanParticipantResponseDTO;
import com.letraaletra.api.presentation.dto.response.websocket.ModerationResponseDTO;
import com.letraaletra.api.presentation.mappers.participant.BanParticipantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class BanParticipantHandler implements RoomRequestHandler<BanParticipantWsRequest> {
    @Autowired
    private BanParticipantUseCase banParticipant;

    @Autowired
    private BanParticipantMapper banParticipantMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(BanParticipantWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        BanParticipantCommand command = banParticipantMapper.toCommand(request, userId);

        BanParticipantOutput output = banParticipant.execute(command);

        BanParticipantResponseDTO dto = banParticipantMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);

        ModerationResponseDTO dtoForBanned = new ModerationResponseDTO("Banned from game");

        gameNotifier.notifierOne(request.participantId(), dtoForBanned);

    }

    @Override
    public Class<BanParticipantWsRequest> getType() {
        return BanParticipantWsRequest.class;
    }
}
