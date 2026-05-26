package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.port.GameQueryService;
import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.application.port.TurnTimeoutManager;
import com.letraaletra.api.application.service.GameOverHandler;
import com.letraaletra.api.application.service.UpdateStatsService;
import com.letraaletra.api.application.usecase.game.PickRandomThemeWordsUseCase;
import com.letraaletra.api.application.usecase.game.*;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.board.service.BoardGenerator;
import com.letraaletra.api.domain.game.service.DefaultGameGenerator;
import com.letraaletra.api.domain.game.service.DefaultGameStateGenerator;
import com.letraaletra.api.domain.game.service.GameStateGenerator;
import com.letraaletra.api.domain.game.service.GenerateRoomCode;
import com.letraaletra.api.domain.repository.game.GameRepository;
import com.letraaletra.api.domain.repository.matchmaking.MatchmakingRepository;
import com.letraaletra.api.domain.repository.theme.ThemeRepository;
import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.infrastructure.manager.GameActorManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class GameUseCaseConfig {
    @Bean
    public CloseRoomDueToTimeoutUseCase closeRoomDueToTimeoutUseCase(
            UserRepository userRepository,
            ActorManager<Game> actorManager,
            GameRepository gameRepository
    ) {
        return new CloseRoomDueToTimeoutUseCase(
                userRepository,
                actorManager,
                gameRepository
        );
    }

    @Bean
    public CreateGameUseCase createGameUseCase(
            UserRepository userRepository,
            GameRepository gameRepository,
            ActorManager<Game> actorManager,
            GameTimeoutManager gameTimeoutManager,
            GameQueryService gameQueryService,
            TokenService tokenService,
            GenerateRoomCode generateRoomCode
    ) {
        return new CreateGameUseCase(
                userRepository,
                gameRepository,
                actorManager,
                gameTimeoutManager,
                gameQueryService,
                tokenService,
                generateRoomCode
        );
    }

    @Bean
    public FindByCodeUseCase findByCodeUseCase(GameQueryService gameQueryService, TokenService tokenService) {
        return new FindByCodeUseCase( gameQueryService, tokenService);
    }

    @Bean
    public FindByTokenGameIdUseCase findByTokenGameIdUseCase(TokenService tokenService, ActorManager<Game> actorManager) {
        return new FindByTokenGameIdUseCase(tokenService, actorManager);
    }

    @Bean
    public GetPublicGamesUseCase getPublicGamesUseCase(GameQueryService gameQueryService, TokenService tokenService) {
        return new GetPublicGamesUseCase(gameQueryService, tokenService);
    }

    @Bean
    public JoinGameUseCase joinGameUseCase(UserRepository userRepository, TokenService tokenService, ActorManager<Game> actorManager) {
        return new JoinGameUseCase(userRepository, tokenService, actorManager);
    }

    @Bean
    public LeftGameUseCase leftGameUseCase(
            TokenService tokenService,
            GameActorManager gameActorManager,
            UserRepository userRepository,
            GameRepository gameRepository
    ) {
        return new LeftGameUseCase(
                tokenService,
                gameActorManager,
                userRepository,
                gameRepository
        );
    }

    @Bean
    public StartGameUseCase startGameUseCase(
             GameStateGenerator gameStateGenerator,
             ThemeRepository themeRepository,
             GameTimeoutManager gameTimeoutManager,
             PickRandomThemeWordsUseCase pickRandomThemeWordsUseCase,
             BoardGenerator boardGenerator,
             TokenService tokenService,
             TurnTimeoutManager turnTimeoutManager,
             GameActorManager gameActorManager
    ) {
        return new StartGameUseCase(
                gameStateGenerator,
                themeRepository,
                gameTimeoutManager,
                pickRandomThemeWordsUseCase,
                boardGenerator,
                tokenService,
                turnTimeoutManager,
                gameActorManager
        );
    }

    @Bean
    public ExpireTurnUseCase expireTurnUseCase(
            GameActorManager gameActorManager,
            GameOverHandler gameOverHandler,
            UserRepository userRepository
    ) {
        return new ExpireTurnUseCase(gameActorManager, gameOverHandler, userRepository);
    }

    @Bean
    public PickRandomThemeWordsUseCase pickRandomThemeWordsUseCase(ThemeRepository themeRepository) {
        return new PickRandomThemeWordsUseCase(themeRepository, new Random());
    }

    @Bean
    public JoinMatchmakingQueueUseCase joinMatchmakingQueueUseCase(
            MatchmakingRepository matchmakingRepository,
            UserRepository userRepository,
            GameRepository gameRepository,
            GameQueryService gameQueryService,
            DefaultGameStateGenerator defaultGameStateGenerator,
            DefaultGameGenerator defaultGameGenerator,
            PickRandomThemeWordsUseCase pickRandomThemeWordsUseCase,
            GenerateRoomCode generateRoomCode,
            TokenService tokenService,
            TurnTimeoutManager turnTimeoutManager,
            ActorManager<Game> actorManager
    ) {
        return new JoinMatchmakingQueueUseCase(
                matchmakingRepository,
                userRepository,
                gameRepository,
                gameQueryService,
                defaultGameStateGenerator,
                defaultGameGenerator,
                pickRandomThemeWordsUseCase,
                generateRoomCode,
                tokenService,
                turnTimeoutManager,
                actorManager
        );
    }

    @Bean
    public GameOverHandler gameOverHandler(
            GameRepository gameRepository,
            UserRepository userRepository,
            ActorManager<Game> actorManager,
            GameTimeoutManager gameTimeoutManager,
            UpdateStatsService updateStatsService
    ) {
        return new GameOverHandler(
                gameRepository,
                userRepository,
                actorManager,
                gameTimeoutManager,
                updateStatsService
        );
    }

    @Bean
    public UpdateStatsService updateStatsService(UserRepository userRepository) {
        return new UpdateStatsService(userRepository);
    }
}
