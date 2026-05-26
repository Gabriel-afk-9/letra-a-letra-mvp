package com.letraaletra.api.infrastructure.persistence.memory;

import com.letraaletra.api.application.port.SessionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Repository
public class InMemorySessionRepository implements SessionRepository {
    private final Map<String, WebSocketSession> sessionsBySessionId = new ConcurrentHashMap<>();
    private final Map<String, String> sessionIdByUserId = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    private ReentrantLock lockFor(String userId) {
        return userLocks.computeIfAbsent(userId, k -> new ReentrantLock());
    }

    @Override
    public void save(WebSocketSession session) {
        WebSocketSession decorated =
                new ConcurrentWebSocketSessionDecorator(session, 10_000, 8192);

        String sessionId = decorated.getId();
        String userId = (String) session.getAttributes().get("userId");

        ReentrantLock lock = lockFor(userId);
        lock.lock();

        try {
            String oldSessionId = sessionIdByUserId.put(userId, sessionId);

            if (oldSessionId != null && !oldSessionId.equals(sessionId)) {
                WebSocketSession oldSession = sessionsBySessionId.remove(oldSessionId);

                if (oldSession != null) {
                    try {
                        if (oldSession.isOpen()) {
                            oldSession.close();
                        }
                    } catch (Exception ignored) {}
                }
            }

            sessionsBySessionId.put(sessionId, decorated);

        } finally {
            lock.unlock();
        }
    }

    @Override
    public WebSocketSession find(String sessionId) {
        return sessionsBySessionId.get(sessionId);
    }

    @Override
    public WebSocketSession findByUserId(String userId) {
        String sessionId = sessionIdByUserId.get(userId);
        if (sessionId == null) return null;
        return sessionsBySessionId.get(sessionId);
    }

    @Override
    public void remove(WebSocketSession session) {
        String sessionId = session.getId();
        String userId = (String) session.getAttributes().get("userId");

        if (userId == null) {
            sessionsBySessionId.remove(sessionId);
            return;
        }

        ReentrantLock lock = lockFor(userId);
        lock.lock();

        try {
            sessionsBySessionId.remove(sessionId);

            String current = sessionIdByUserId.get(userId);
            if (sessionId.equals(current)) {
                sessionIdByUserId.remove(userId);
            }

        } finally {
            lock.unlock();
        }
    }
}