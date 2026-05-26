package com.letraaletra.api.infrastructure.scheduler;

import com.letraaletra.api.application.command.game.CloseRoomCommand;
import com.letraaletra.api.application.output.game.CloseRoomOutput;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.application.usecase.game.CloseRoomDueToTimeoutUseCase;
import com.letraaletra.api.domain.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class ScheduledGameTimeoutManager implements GameTimeoutManager {
    @Autowired
    private CloseRoomDueToTimeoutUseCase closeRoomDueToTimeoutUseCase;

    @Autowired
    private GameNotifier gameNotifier;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private final Map<String, ScheduledFuture<?>> timers = new ConcurrentHashMap<>();

    public void start(Game game) {
        cancel(game);

        ScheduledFuture<?> future = scheduler.schedule(() -> handleTimeout(game), 5, TimeUnit.MINUTES);

        timers.put(game.getId(), future);
    }

    public void cancel(Game game) {
        ScheduledFuture<?> future = timers.remove(game.getId());

        if (future != null) {
            future.cancel(false);
        }
    }

    private void handleTimeout(Game game) {
        timers.remove(game.getId());

        CloseRoomOutput output = closeRoomDueToTimeoutUseCase.execute(
                new CloseRoomCommand(game)
        );

        RoomClosed data = new RoomClosed(output.event(), output.reason());

        gameNotifier.notifierAll(output.game(), data);
    }
}
