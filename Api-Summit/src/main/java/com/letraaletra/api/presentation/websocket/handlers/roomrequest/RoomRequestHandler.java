package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.presentation.dto.request.WsRequestDTO;
import org.springframework.web.socket.WebSocketSession;

public interface RoomRequestHandler<T extends WsRequestDTO> {
    void handle(T request, WebSocketSession session);

    Class<T> getType();
}
