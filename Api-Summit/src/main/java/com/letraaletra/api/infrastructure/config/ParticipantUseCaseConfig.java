package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.application.context.ModerationContextFactory;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.port.DisconnectScheduler;
import com.letraaletra.api.application.usecase.participant.*;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.game.GameRepository;
import com.letraaletra.api.domain.repository.matchmaking.MatchmakingRepository;
import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.infrastructure.manager.GameActorManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParticipantUseCaseConfig {
    @Bean
    public BanParticipantUseCase banParticipantUseCase(
            ModerationContextFactory moderationContextFactory,
            UserRepository userRepository,
            GameActorManager gameActorManager
    ) {
        return new BanParticipantUseCase(
                moderationContextFactory,
                userRepository,
                gameActorManager
        );
    }

    @Bean
    public DisconnectUseCase disconnectUseCase(
            GameActorManager gameActorManager,
            DisconnectScheduler disconnectScheduler,
            MatchmakingRepository matchmakingRepository,
            UserRepository userRepository
    ) {
        return new DisconnectUseCase(
                gameActorManager,
                disconnectScheduler,
                matchmakingRepository,
                userRepository
        );
    }
    @Bean
    public KickParticipantUseCase kickParticipantUseCase(
            ModerationContextFactory moderationContextFactory,
            GameActorManager gameActorManager
    ) {
        return new KickParticipantUseCase(moderationContextFactory, gameActorManager);
    }

    @Bean
    public ReconnectUseCase reconnectUseCase(
            ActorManager<Game> actorManager,
            DisconnectScheduler disconnectScheduler,
            UserRepository userRepository
            ) {
        return new ReconnectUseCase(
                actorManager,
                disconnectScheduler,
                userRepository
        );
    }

    @Bean
    public SwapRoomPositionUseCase swapRoomPositionUseCase(GameActorManager gameActorManager, TokenService tokenService) {
        return new SwapRoomPositionUseCase(gameActorManager, tokenService);
    }

    @Bean
    public UnbanUserUseCase unbanUserUseCase(TokenService tokenService, GameActorManager gameActorManager) {
        return new UnbanUserUseCase(tokenService, gameActorManager);
    }

    @Bean
    public RemoveParticipantUseCase removeParticipantUseCase(
            GameActorManager gameActorManager,
            GameRepository gameRepository,
            UserRepository userRepository
    ) {
        return new RemoveParticipantUseCase(gameActorManager, gameRepository, userRepository);
    }
}
