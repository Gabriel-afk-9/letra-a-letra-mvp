package com.letraaletra.api.presentation.websocket.dispatcher;

import com.letraaletra.api.presentation.dto.request.player.PlayerActionDTO;
import com.letraaletra.api.domain.game.player.exception.InvalidPlayerActionException;
import com.letraaletra.api.presentation.websocket.handlers.action.InGameActionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PlayerActionRequestDispatcher {
    private final Map<Class<?>, InGameActionHandler<?>> handlers = new HashMap<>();

    @Autowired
    public PlayerActionRequestDispatcher(List<InGameActionHandler<?>> handlerList) {
        for (InGameActionHandler<?> handler : handlerList) {
            handlers.put(handler.getType(), handler);
        }
    }

    @SuppressWarnings("unchecked")
    public void dispatch(String gameTokenId, PlayerActionDTO request, WebSocketSession session) {
        InGameActionHandler<PlayerActionDTO> handler =
                (InGameActionHandler<PlayerActionDTO>) handlers.get(request.getClass());

        if (handler == null) {
            throw new InvalidPlayerActionException();
        }

        handler.handle(request, session, gameTokenId);
    }
}
