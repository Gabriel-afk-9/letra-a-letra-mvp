package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.application.command.game.JoinGameCommand;
import com.letraaletra.api.presentation.dto.response.websocket.JoinGameResponseDTO;
import com.letraaletra.api.application.output.game.JoinGameOutput;
import com.letraaletra.api.presentation.dto.request.JoinGameWsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JoinGameMapper {
    @Autowired
    private GameDTOMapper gameDTOMapper;

    public JoinGameCommand toCommand(JoinGameWsRequest request, String sessionId, String userId) {
        return new JoinGameCommand(
                request.tokenGameId(),
                sessionId,
                userId
        );
    }

    public JoinGameResponseDTO toResponseDTO(JoinGameOutput output) {
        return new JoinGameResponseDTO(
                gameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
