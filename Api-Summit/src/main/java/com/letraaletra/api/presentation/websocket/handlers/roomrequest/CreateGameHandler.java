package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.command.game.CreateGameCommand;
import com.letraaletra.api.application.output.game.CreateGameOutput;
import com.letraaletra.api.application.usecase.game.CreateGameUseCase;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.presentation.dto.request.CreateGameWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.CreateGameResponseDTO;
import com.letraaletra.api.presentation.mappers.game.CreateGameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class CreateGameHandler implements RoomRequestHandler<CreateGameWsRequest> {
    @Autowired
    private CreateGameMapper createGameMapper;

    @Autowired
    private CreateGameUseCase createGame;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(CreateGameWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        CreateGameCommand command = createGameMapper.toCommand(request, session.getId(), userId);

        CreateGameOutput output = createGame.execute(command);

        CreateGameResponseDTO dto = createGameMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);
    }

    @Override
    public Class<CreateGameWsRequest> getType() {
        return CreateGameWsRequest.class;
    }
}
