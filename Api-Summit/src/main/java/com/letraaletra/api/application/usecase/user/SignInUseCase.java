package com.letraaletra.api.application.usecase.user;

import com.letraaletra.api.application.command.user.SignInCommand;
import com.letraaletra.api.application.output.user.SignInOutput;
import com.letraaletra.api.application.usecase.UseCase;
import com.letraaletra.api.domain.security.PasswordService;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.security.exceptions.InvalidPasswordException;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import com.letraaletra.api.domain.repository.user.UserRepository;

public class SignInUseCase implements UseCase<SignInCommand, SignInOutput> {
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    public SignInUseCase(UserRepository userRepository, PasswordService passwordService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
    }

    public SignInOutput execute(SignInCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(UserNotFoundException::new);

        checkMatch(command.password(), user.getHashPassword());

        String token = tokenService.generateToken(user.getId());

        return buildReturn(user.getId(), token);
    }

    private void checkMatch(String password, String hash) {
        boolean matches = passwordService.matches(password, hash);

        if (!matches) {
            throw new InvalidPasswordException();
        }
    }

    private SignInOutput buildReturn(String userId, String token) {
        return new SignInOutput(
                userId,
                token
        );
    }
}
