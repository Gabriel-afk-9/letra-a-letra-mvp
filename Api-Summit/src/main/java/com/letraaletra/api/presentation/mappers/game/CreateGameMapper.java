package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.application.command.game.CreateGameCommand;
import com.letraaletra.api.application.output.game.CreateGameOutput;
import com.letraaletra.api.domain.game.RoomSettings;
import com.letraaletra.api.presentation.dto.request.CreateGameWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.CreateGameResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateGameMapper {
    @Autowired
    private GameDTOMapper gameDTOMapper;

    public CreateGameCommand toCommand(CreateGameWsRequest request, String sessionId, String userId) {
        return new CreateGameCommand(
                request.name(),
                new RoomSettings(
                        request.settings().allowSpectators(),
                        request.settings().privateGame()
                ),
                sessionId,
                userId
        );
    }

    public CreateGameResponseDTO toResponseDTO(CreateGameOutput output) {
        return new CreateGameResponseDTO(
               gameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
