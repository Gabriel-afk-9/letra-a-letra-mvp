package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.JoinMatchmakingCommand;
import com.letraaletra.api.application.output.game.JoinMatchmakingOutput;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.port.GameQueryService;
import com.letraaletra.api.application.port.TurnTimeoutManager;
import com.letraaletra.api.application.usecase.UseCase;
import com.letraaletra.api.domain.game.*;
import com.letraaletra.api.domain.game.matchmaking.MatchmakingUser;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.factory.ParticipantFactory;
import com.letraaletra.api.domain.game.service.DefaultGameGenerator;
import com.letraaletra.api.domain.game.service.DefaultGameResult;
import com.letraaletra.api.domain.game.service.DefaultGameStateGenerator;
import com.letraaletra.api.domain.game.service.GenerateRoomCode;
import com.letraaletra.api.domain.game.state.GameMode;
import com.letraaletra.api.domain.game.state.GameState;
import com.letraaletra.api.domain.repository.game.GameRepository;
import com.letraaletra.api.domain.repository.matchmaking.MatchmakingRepository;
import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class JoinMatchmakingQueueUseCase implements UseCase<JoinMatchmakingCommand, JoinMatchmakingOutput> {
    private final MatchmakingRepository matchmakingRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final GameQueryService gameQueryService;
    private final DefaultGameStateGenerator defaultGameStateGenerator;
    private final DefaultGameGenerator defaultGameGenerator;
    private final PickRandomThemeWordsUseCase pickRandomThemeWordsUseCase;
    private final GenerateRoomCode generateRoomCode;
    private final TokenService tokenService;
    private final TurnTimeoutManager turnTimeoutManager;
    private final ActorManager<Game> actorManager;

    private final Map<GameMode, Object> locks = new ConcurrentHashMap<>();

    public JoinMatchmakingQueueUseCase(
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
        this.matchmakingRepository = matchmakingRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.gameQueryService = gameQueryService;
        this.defaultGameStateGenerator = defaultGameStateGenerator;
        this.defaultGameGenerator = defaultGameGenerator;
        this.pickRandomThemeWordsUseCase = pickRandomThemeWordsUseCase;
        this.generateRoomCode = generateRoomCode;
        this.tokenService = tokenService;
        this.turnTimeoutManager = turnTimeoutManager;
        this.actorManager = actorManager;
    }

    public JoinMatchmakingOutput execute(JoinMatchmakingCommand command) {
        MatchmakingUser matchmakingUser = command.matchmakingUser();

        User user = userRepository.find(matchmakingUser.user()).orElse(null);
        validateUser(user);

        Object lock = locks.computeIfAbsent(command.gameMode(), k -> new Object());

        synchronized (lock) {
            MatchmakingUser matchmakingOpponent = matchmakingRepository.poll(command.gameMode());

            if (matchmakingOpponent == null) {
                matchmakingRepository.add(matchmakingUser, command.gameMode());
                return buildOutput(new DefaultGameResult(null));
            }

            User opponent = userRepository.find(matchmakingOpponent.user()).orElse(null);

            validateUser(opponent);

            Participant player1 = ParticipantFactory.fromUser(opponent, matchmakingOpponent.session());

            Participant player2 = ParticipantFactory.fromUser(user, matchmakingUser.session());

            DefaultGameResult result = defaultGameGenerator.generate(player1, player2, getCode());

            gameRepository.save(result.game());

            actorManager.create(result.game().getId(), result.game());

            user.enterGame(result.game().getId());
            opponent.enterGame(result.game().getId());

            userRepository.save(user);
            userRepository.save(opponent);

            startDefaultGame(result.game(), command.gameMode());

            return buildOutput(result);
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private void startDefaultGame(Game game, GameMode gameMode) {
        List<String> words = pickRandomThemeWordsUseCase.execute();

        GameState state = defaultGameStateGenerator.generate(game, gameMode, words);

        game.setGameStatus(GameStatus.RUNNING);

        game.updateGameState(state);

        turnTimeoutManager.start(game);

        gameRepository.save(game);
    }

    private String getCode() {
        String code;

        do {
            code = generateRoomCode.execute();

        } while (gameQueryService.existsByCode(code));

        return code;
    }

    private JoinMatchmakingOutput buildOutput(DefaultGameResult result) {
        return new JoinMatchmakingOutput(
                result.game() != null ? Optional.of(tokenService.generateToken(result.game().getId())) : Optional.empty(),
                result.game() != null ? Optional.of(result.game()) : Optional.empty()
        );
    }
}
