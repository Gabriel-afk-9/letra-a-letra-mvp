package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.presentation.dto.request.PlayerActionWsRequest;
import com.letraaletra.api.presentation.websocket.dispatcher.PlayerActionRequestDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class PlayerActionHandler implements RoomRequestHandler<PlayerActionWsRequest> {
    @Autowired
    private PlayerActionRequestDispatcher dispatcher;

    @Override
    public void handle(PlayerActionWsRequest request, WebSocketSession session) {
        dispatcher.dispatch(request.tokenGameId(), request.action(), session);
    }

    @Override
    public Class<PlayerActionWsRequest> getType() {
        return PlayerActionWsRequest.class;
    }
}
