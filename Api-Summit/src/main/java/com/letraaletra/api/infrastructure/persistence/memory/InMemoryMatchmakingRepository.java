package com.letraaletra.api.infrastructure.persistence.memory;

import com.letraaletra.api.domain.game.state.GameMode;
import com.letraaletra.api.domain.game.matchmaking.MatchmakingUser;
import com.letraaletra.api.domain.repository.matchmaking.MatchmakingRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class InMemoryMatchmakingRepository implements MatchmakingRepository {
    private final Map<GameMode, Queue<MatchmakingUser>> queues = new ConcurrentHashMap<>();
    private final Map<String, MatchmakingUser> users = new ConcurrentHashMap<>();

    public InMemoryMatchmakingRepository() {
        for (GameMode mode : GameMode.values()) {
            queues.put(mode, new ConcurrentLinkedDeque<>());
        }
    }

    @Override
    public void add(MatchmakingUser matchmakingUser, GameMode gameMode) {
        if (users.putIfAbsent(matchmakingUser.user(), matchmakingUser) == null) {
            queues.get(gameMode).add(matchmakingUser);
        }
    }

    @Override
    public void remove(MatchmakingUser matchmakingUser) {
        removeById(matchmakingUser.user());
    }

    @Override
    public void removeById(String id) {
        MatchmakingUser user = users.remove(id);

        if (user == null) return;

        for (Queue<MatchmakingUser> queue : queues.values()) {
            queue.remove(user);
        }
    }

    @Override
    public MatchmakingUser poll(GameMode gameMode) {
        MatchmakingUser user = queues.get(gameMode).poll();

        if (user != null) {
            users.remove(user.user());
        }

        return user;
    }

    @Override
    public boolean onQueue(String userId) {
        return users.containsKey(userId);
    }
}
