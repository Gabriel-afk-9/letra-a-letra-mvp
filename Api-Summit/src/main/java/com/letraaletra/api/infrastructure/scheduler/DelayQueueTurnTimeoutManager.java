package com.letraaletra.api.infrastructure.scheduler;

import com.letraaletra.api.application.command.game.ExpireTurnCommand;
import com.letraaletra.api.application.output.game.ExpireTurnOutput;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.application.port.TurnTimeoutManager;
import com.letraaletra.api.application.usecase.game.ExpireTurnUseCase;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.DelayQueue;

@Service
public class DelayQueueTurnTimeoutManager implements TurnTimeoutManager {
    private final ExpireTurnUseCase expireTurnUseCase;

    private final DelayQueue<GameTurn> queue = new DelayQueue<>();

    private final GameNotifier gameNotifier;

    private final Logger logger = LoggerFactory.getLogger(DelayQueueTurnTimeoutManager.class);

    public DelayQueueTurnTimeoutManager(ExpireTurnUseCase expireTurnUseCase, GameNotifier gameNotifier) {
        this.expireTurnUseCase = expireTurnUseCase;
        this.gameNotifier = gameNotifier;
        startScheduler();
    }

    @Override
    public void start(Game game) {
        queue.put(new GameTurn(
                game.getId(),
                game.getGameState().getCurrentTurnEnds(),
                game.getGameState().getVersion()
        ));
    }

    private void startScheduler() {
        Thread thread = new Thread(this::processLoop);
        thread.setDaemon(true);
        thread.start();
    }

    private void processLoop() {
        while (true) {
            try {
                GameTurn next = queue.take();
                handleTurnTimeout(next);

            } catch (Exception e) {
                logger.warn("Error on process end of turn {}-{}", e.getMessage(), e.getStackTrace());
            }
        }
    }

    private void handleTurnTimeout(GameTurn gameTurn) {
        ExpireTurnCommand command = new ExpireTurnCommand(gameTurn.gameId(), gameTurn.version());

        Optional<ExpireTurnOutput> output = expireTurnUseCase.execute(command);

        if (output.isEmpty()) return;

        ExpireTurnOutput result = output.get();

        TurnExpired data = new TurnExpired(
                result.event(),
                new TurnExpired.ExpiredData(result.user(), result.currentPlayerTurnId())
        );

        gameNotifier.notifierAll(result.game(), data);

        if (result.removedBecauseAfk()) {
            gameNotifier.notifierOne(
                    result.user(),
                    new RemovedBecauseInactivity("REMOVED_BECAUSE_INACTIVITY")
            );
        }

        if (result.gameOverResult().finished()) {
            gameNotifier.notifierGameOver(result.game(), result.gameOverResult());
        }

        if (result.game().getGameStatus().equals(GameStatus.RUNNING)) {
            start(result.game());
        }
    }
}
