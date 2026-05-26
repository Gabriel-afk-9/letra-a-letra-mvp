package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.application.command.game.LeftGameCommand;
import com.letraaletra.api.application.output.game.LeftGameOutput;
import com.letraaletra.api.presentation.dto.request.LeftGameWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.LeftGameResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeftGameMapper {
    @Autowired
    private GameDTOMapper gameDTOMapper;

    public LeftGameCommand toCommand(LeftGameWsRequest request, String session) {
        return new LeftGameCommand(
                request.tokenGameId(),
                session
        );
    }

    public LeftGameResponseDTO toResponseDTO(LeftGameOutput output) {
        return new LeftGameResponseDTO(
                gameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
