package com.letraaletra.api.infrastructure.actor;

import com.letraaletra.api.application.command.actor.ActorCommand;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.domain.game.Game;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameActor implements Actor {
    private final Queue<CommandEnvelope<?>> mailbox = new ConcurrentLinkedQueue<>();
    private final ExecutorService executor;
    private final Game game;
    private final AtomicBoolean processing = new AtomicBoolean(false);

    private record CommandEnvelope<T>(
            ActorCommand<T> command,
            CompletableFuture<T> future
    ) {}

    public GameActor(ExecutorService executor, Game game) {
        this.game = game;
        this.executor = executor;
    }

    @Override
    public <T> CompletableFuture<T> enqueueCommand(ActorCommand<T> command) {
        CompletableFuture<T> future = new CompletableFuture<>();
        mailbox.add(new CommandEnvelope<>(command, future));
        trySchedule();
        return future;
    }

    private void trySchedule() {
        if (processing.compareAndSet(false, true)) {
            executor.submit(this::processQueue);
        }
    }

    private void processQueue() {
        try {
            CommandEnvelope<?> envelope;
            while ((envelope = mailbox.poll()) != null) {
                executeEnvelope(envelope);
            }
        } finally {
            processing.set(false);

            if (!mailbox.isEmpty()) {
                trySchedule();
            }
        }
    }

    private <T> void executeEnvelope(CommandEnvelope<T> envelope) {
        try {
            T result = envelope.command().execute(game);

            envelope.future().complete(result);

        } catch (Throwable t) {
            envelope.future().completeExceptionally(t);
        }
    }

    @Override
    public Game getGame() {
        return game;
    }
}
