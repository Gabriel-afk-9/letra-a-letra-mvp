package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.command.participant.UnbanParticipantCommand;
import com.letraaletra.api.application.output.participant.UnbanParticipantOutput;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.application.usecase.participant.UnbanUserUseCase;
import com.letraaletra.api.presentation.dto.request.UnbanParticipantWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.UnbanParticipantResponseDTO;
import com.letraaletra.api.presentation.mappers.participant.UnbanParticipantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class UnbanParticipantHandler implements RoomRequestHandler<UnbanParticipantWsRequest> {

    @Autowired
    private UnbanUserUseCase unbanUser;

    @Autowired
    private UnbanParticipantMapper unbanParticipantMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(UnbanParticipantWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        UnbanParticipantCommand command = unbanParticipantMapper.toCommand(request, userId);

        UnbanParticipantOutput output = unbanUser.execute(command);

        UnbanParticipantResponseDTO dto = unbanParticipantMapper.toResponseDTO(output);

        gameNotifier.notifierOne(userId, dto);
    }

    @Override
    public Class<UnbanParticipantWsRequest> getType() {
        return UnbanParticipantWsRequest.class;
    }
}
