package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.command.game.StartGameCommand;
import com.letraaletra.api.application.output.game.StartGameOutput;
import com.letraaletra.api.application.usecase.game.StartGameUseCase;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.presentation.dto.request.StartGameWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.StartGameResponseDTO;
import com.letraaletra.api.presentation.mappers.game.StartGameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class StartGameHandler implements RoomRequestHandler<StartGameWsRequest> {
    @Autowired
    private StartGameUseCase startGame;

    @Autowired
    private StartGameMapper startGameMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(StartGameWsRequest request, WebSocketSession session) {
        StartGameCommand command = startGameMapper.toCommand(request, session.getId());

        StartGameOutput output = startGame.execute(command);

        StartGameResponseDTO dto = startGameMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);
    }

    @Override
    public Class<StartGameWsRequest> getType() {
        return StartGameWsRequest.class;
    }
}
