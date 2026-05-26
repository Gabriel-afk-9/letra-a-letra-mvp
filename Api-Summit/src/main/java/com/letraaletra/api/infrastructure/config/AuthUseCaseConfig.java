package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.application.port.GoogleTokenService;
import com.letraaletra.api.application.usecase.auth.GoogleAuthUseCase;
import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.user.factory.UserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {
    @Bean
    public GoogleAuthUseCase authUseCase(
            TokenService tokenService,
            UserRepository userRepository,
            GoogleTokenService googleTokenService,
            UserFactory userFactory
    ) {
        return new GoogleAuthUseCase(
                tokenService,
                userRepository,
                googleTokenService,
                userFactory
        );
    }
}
