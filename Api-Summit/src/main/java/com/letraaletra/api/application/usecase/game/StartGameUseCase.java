package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.actor.StartGameActorCommand;
import com.letraaletra.api.application.command.game.StartGameCommand;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.application.output.game.StartGameOutput;
import com.letraaletra.api.application.port.TurnTimeoutManager;
import com.letraaletra.api.application.usecase.UseCase;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.board.Board;
import com.letraaletra.api.domain.game.board.service.BoardGenerator;
import com.letraaletra.api.domain.game.service.GameStateGenerator;
import com.letraaletra.api.domain.repository.theme.ThemeRepository;
import com.letraaletra.api.domain.game.board.theme.Theme;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class StartGameUseCase implements UseCase<StartGameCommand, StartGameOutput> {
    private final GameStateGenerator gameStateGenerator;
    private final ThemeRepository themeRepository;
    private final GameTimeoutManager gameTimeoutManager;
    private final PickRandomThemeWordsUseCase pickRandomThemeWordsUseCase;
    private final BoardGenerator boardGenerator;
    private final TokenService tokenService;
    private final TurnTimeoutManager turnTimeoutManager;
    private final ActorManager<Game> gameActorManager;

    public StartGameUseCase(
            GameStateGenerator gameStateGenerator,
            ThemeRepository themeRepository,
            GameTimeoutManager gameTimeoutManager,
            PickRandomThemeWordsUseCase pickRandomThemeWordsUseCase,
            BoardGenerator boardGenerator,
            TokenService tokenService,
            TurnTimeoutManager turnTimeoutManager,
            ActorManager<Game> gameActorManager
    ) {
        this.gameStateGenerator = gameStateGenerator;
        this.themeRepository = themeRepository;
        this.gameTimeoutManager = gameTimeoutManager;
        this.pickRandomThemeWordsUseCase = pickRandomThemeWordsUseCase;
        this.boardGenerator = boardGenerator;
        this.tokenService = tokenService;
        this.turnTimeoutManager = turnTimeoutManager;
        this.gameActorManager = gameActorManager;
    }

    public StartGameOutput execute(StartGameCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Theme theme = themeRepository.findById(command.settings().getThemeId());

        List<String> words = (theme != null)
                ? theme.pickRandomWords(5, new Random())
                : pickRandomThemeWordsUseCase.execute();

        Board board = boardGenerator.generate(words, command.settings().getGameMode());

        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<Game> future = actor.enqueueCommand(new StartGameActorCommand(command.session(), board, gameStateGenerator, gameTimeoutManager, turnTimeoutManager));

        Game game = future.join();

        return buildOutput(game, gameId);
    }

    private StartGameOutput buildOutput(Game game, String id) {
        return new StartGameOutput(
                id,
                game
        );
    }
}
