package com.letraaletra.api.infrastructure.manager;

import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.infrastructure.actor.GameActor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Component
public class GameActorManager implements ActorManager<Game> {
    private final Map<String, Actor> actors = new ConcurrentHashMap<>();
    private final ExecutorService executor;

    public GameActorManager(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void create(String id, Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }

        Actor newActor = new GameActor(executor, game);
        actors.putIfAbsent(id, newActor);
    }

    @Override
    public Actor get(String id) {
        Actor existingActor = actors.get(id);

        if (existingActor == null) {
            throw new GameNotFoundException();
        }

        return existingActor;
    }

    @Override
    public List<Actor> getAllActors() {
        return List.copyOf(actors.values());
    }

    @Override
    public void remove(String id) {
        actors.remove(id);
    }
}
