package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.command.game.LeftGameCommand;
import com.letraaletra.api.application.output.game.LeftGameOutput;
import com.letraaletra.api.application.usecase.game.LeftGameUseCase;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.presentation.dto.request.LeftGameWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.LeftGameResponseDTO;
import com.letraaletra.api.presentation.mappers.game.LeftGameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class LeftGameHandler implements RoomRequestHandler<LeftGameWsRequest> {
    @Autowired
    private LeftGameUseCase leftGame;

    @Autowired
    private LeftGameMapper leftGameMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(LeftGameWsRequest request, WebSocketSession session) {
        LeftGameCommand command = leftGameMapper.toCommand(request, session.getId());

        LeftGameOutput output = leftGame.execute(command);

        LeftGameResponseDTO dto = leftGameMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);

        if (output.gameOverResult() != null) {
            gameNotifier.notifierGameOver(output.game(), output.gameOverResult());
        }
    }

    @Override
    public Class<LeftGameWsRequest> getType() {
        return LeftGameWsRequest.class;
    }
}
