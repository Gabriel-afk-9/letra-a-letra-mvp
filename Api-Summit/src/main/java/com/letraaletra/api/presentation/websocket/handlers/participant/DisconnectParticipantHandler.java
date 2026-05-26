package com.letraaletra.api.presentation.websocket.handlers.participant;

import com.letraaletra.api.application.command.participant.DisconnectParticipantCommand;
import com.letraaletra.api.application.output.participant.DisconnectParticipantOutput;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.application.usecase.participant.DisconnectUseCase;
import com.letraaletra.api.presentation.dto.response.websocket.DisconnectParticipantResponseDTO;
import com.letraaletra.api.presentation.mappers.participant.DisconnectParticipantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Component
public class DisconnectParticipantHandler {
    @Autowired
    private DisconnectParticipantMapper disconnectParticipantMapper;

    @Autowired
    private DisconnectUseCase disconnectUseCase;

    @Autowired
    private GameNotifier gameNotifier;

    public void handler(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        DisconnectParticipantCommand command = disconnectParticipantMapper.toCommand(userId, session.getId());

        Optional<DisconnectParticipantOutput> output = disconnectUseCase.execute(command);

        output.ifPresent(out -> {
            DisconnectParticipantResponseDTO dto = disconnectParticipantMapper.toResponseDTO(out);

            gameNotifier.notifierAll(out.game(), dto);
        });
    }
}
