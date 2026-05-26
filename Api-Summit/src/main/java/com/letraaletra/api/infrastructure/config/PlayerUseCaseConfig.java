package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.application.port.TurnTimeoutManager;
import com.letraaletra.api.application.service.GameOverHandler;
import com.letraaletra.api.application.usecase.player.DiscardPowerUseCase;
import com.letraaletra.api.application.usecase.player.PlayerActionUseCase;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.infrastructure.manager.GameActorManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlayerUseCaseConfig {
    @Bean
    public PlayerActionUseCase playerActionUseCase(
            TokenService tokenService,
            GameTimeoutManager gameTimeoutManager,
            TurnTimeoutManager turnTimeoutManager,
            GameActorManager gameActorManager,
            GameOverHandler gameOverHandler
            ) {
        return new PlayerActionUseCase(
                tokenService,
                gameTimeoutManager,
                turnTimeoutManager,
                gameActorManager,
                gameOverHandler
        );
    }

    @Bean
    public DiscardPowerUseCase discardPowerUseCase(TokenService tokenService, GameActorManager gameActorManager) {
        return new DiscardPowerUseCase(tokenService, gameActorManager);
    }
}
