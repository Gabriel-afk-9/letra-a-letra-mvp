package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.application.service.SelectNicknameService;
import com.letraaletra.api.application.usecase.user.*;
import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.security.PasswordService;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.user.factory.UserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class UserUseCaseConfig {
    @Bean
    public CreateUserUseCase createUserUseCase(
            UserRepository userRepository,
            PasswordService passwordService,
            UserFactory userFactory,
            SelectNicknameService selectNicknameService
    ) {
        return new CreateUserUseCase(userRepository, passwordService, userFactory, selectNicknameService);
    }

    @Bean
    public FindUserByIdUseCase findUserByIdUseCase(UserRepository userRepository) {
        return new FindUserByIdUseCase(userRepository);
    }

    @Bean
    public SignInUseCase signInUseCase(UserRepository userRepository, PasswordService passwordService, TokenService tokenService) {
        return new SignInUseCase(userRepository, passwordService, tokenService);
    }

    @Bean
    public SetNicknameUseCase setNicknameUseCase(UserRepository userRepository) {
        return new SetNicknameUseCase(userRepository);
    }

    @Bean
    public SelectNicknameService selectNicknameService(UserRepository userRepository, Random random) {
        return new SelectNicknameService(userRepository, random);
    }

    @Bean
    public SetAvatarUseCase setAvatarUseCase(UserRepository userRepository) {
        return new SetAvatarUseCase(userRepository);
    }
}
