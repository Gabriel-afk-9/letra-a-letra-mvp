package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.command.game.JoinGameCommand;
import com.letraaletra.api.application.output.game.JoinGameOutput;
import com.letraaletra.api.application.usecase.game.JoinGameUseCase;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.presentation.dto.request.JoinGameWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.JoinGameResponseDTO;
import com.letraaletra.api.presentation.mappers.game.JoinGameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class JoinGameHandler implements RoomRequestHandler<JoinGameWsRequest> {
    @Autowired
    private JoinGameUseCase joinGame;

    @Autowired
    private JoinGameMapper joinGameMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(JoinGameWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        JoinGameCommand command = joinGameMapper.toCommand(request, session.getId(), userId);

        JoinGameOutput output = joinGame.execute(command);

        JoinGameResponseDTO dto = joinGameMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);
    }

    @Override
    public Class<JoinGameWsRequest> getType() {
        return JoinGameWsRequest.class;
    }
}

