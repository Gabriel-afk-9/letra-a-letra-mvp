package com.letraaletra.api.application.port;

import org.springframework.web.socket.WebSocketSession;

public interface SessionRepository {
    void save(WebSocketSession webSocketSession);
    WebSocketSession findByUserId(String userId);
    WebSocketSession find(String sessionId);
    void remove(WebSocketSession webSocketSession);
}
