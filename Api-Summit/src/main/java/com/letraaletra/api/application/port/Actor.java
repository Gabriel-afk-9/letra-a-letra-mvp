package com.letraaletra.api.application.port;

import com.letraaletra.api.application.command.actor.ActorCommand;
import com.letraaletra.api.domain.game.Game;

import java.util.concurrent.CompletableFuture;

public interface Actor {
     <T> CompletableFuture<T> enqueueCommand(ActorCommand<T> command);
     Game getGame();
}
