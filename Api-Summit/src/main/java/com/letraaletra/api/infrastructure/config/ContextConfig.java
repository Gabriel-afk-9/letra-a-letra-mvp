package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.application.context.ModerationContextFactory;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.security.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContextConfig {
    @Bean
    public ModerationContextFactory moderationContextFactory(ActorManager<Game> actorManager, TokenService tokenService) {
        return new ModerationContextFactory(actorManager, tokenService);
    }
}
