package com.letraaletra.api.presentation.websocket.handlers.action;

import com.letraaletra.api.presentation.dto.request.player.PlayerActionDTO;
import org.springframework.web.socket.WebSocketSession;

public interface InGameActionHandler<T extends PlayerActionDTO> {
    void handle(T request, WebSocketSession session, String gameTokenId);

    Class<T> getType();
}
