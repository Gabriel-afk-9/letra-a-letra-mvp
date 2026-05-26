package com.letraaletra.api.presentation.websocket.handlers.participant;

import com.letraaletra.api.application.command.participant.ReconnectParticipantCommand;
import com.letraaletra.api.application.output.participant.ReconnectParticipantOutput;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.application.usecase.participant.ReconnectUseCase;
import com.letraaletra.api.presentation.dto.response.websocket.ReconnectParticipantResponseDTO;
import com.letraaletra.api.presentation.mappers.participant.ReconnectParticipantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Component
public class ReconnectParticipantHandler {
    @Autowired
    private ReconnectParticipantMapper reconnectParticipantMapper;

    @Autowired
    private ReconnectUseCase reconnectUseCase;

    @Autowired
    private GameNotifier gameNotifier;

    public void handle(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        ReconnectParticipantCommand command = reconnectParticipantMapper.toCommand(userId, session.getId());

        Optional<ReconnectParticipantOutput> output = reconnectUseCase.execute(command);

        output.ifPresent(out -> {
            ReconnectParticipantResponseDTO dto = reconnectParticipantMapper.toResponseDTO(out);

            gameNotifier.notifierAll(out.game(), dto);
        });
    }
}
