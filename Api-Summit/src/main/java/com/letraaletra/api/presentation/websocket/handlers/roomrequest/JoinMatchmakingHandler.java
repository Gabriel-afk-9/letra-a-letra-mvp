package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.command.game.JoinMatchmakingCommand;
import com.letraaletra.api.application.output.game.JoinMatchmakingOutput;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.application.usecase.game.JoinMatchmakingQueueUseCase;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.presentation.dto.request.JoinMatchmakingGameWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.JoinMatchmakingResponseDTO;
import com.letraaletra.api.presentation.mappers.game.JoinMatchmakingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class JoinMatchmakingHandler implements RoomRequestHandler<JoinMatchmakingGameWsRequest> {
    @Autowired
    private JoinMatchmakingQueueUseCase joinMatchmakingQueueUseCase;

    @Autowired
    private JoinMatchmakingMapper joinMatchmakingMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(JoinMatchmakingGameWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        JoinMatchmakingCommand command = joinMatchmakingMapper.toCommand(userId, session.getId(), request.gameMode());

        JoinMatchmakingOutput output = joinMatchmakingQueueUseCase.execute(command);

        JoinMatchmakingResponseDTO dto = joinMatchmakingMapper.toResponseDTO(output);

        notifier(output.game().orElse(null), userId, dto);
    }

    @Override
    public Class<JoinMatchmakingGameWsRequest> getType() {
        return JoinMatchmakingGameWsRequest.class;
    }

    private void notifier(Game game, String user, JoinMatchmakingResponseDTO dto) {
        if (game == null) {
            gameNotifier.notifierOne(user, dto);

        } else {
            gameNotifier.notifierAll(game, dto);
        }
    }
}
