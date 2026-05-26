package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.application.command.game.StartGameCommand;
import com.letraaletra.api.application.output.game.StartGameOutput;
import com.letraaletra.api.domain.game.state.GameSettings;
import com.letraaletra.api.presentation.dto.request.StartGameWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.StartGameResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartGameMapper {

    @Autowired
    private GameStateDTOMapper gameStateDTOMapper;

    public StartGameCommand toCommand(StartGameWsRequest request, String session) {
        return new StartGameCommand(
                request.tokenGameId(),
                session,
                new GameSettings(
                        request.settings().themeId(),
                        request.settings().gameMode()
                )
        );
    }

    public StartGameResponseDTO toResponseDTO(StartGameOutput output) {
        return new StartGameResponseDTO(
                gameStateDTOMapper.toAllDTO(output.game())
        );
    }
}
