package com.letraaletra.api.application.usecase.user;

import com.letraaletra.api.application.command.user.CreateUserCommand;
import com.letraaletra.api.application.output.user.CreateUserOutput;
import com.letraaletra.api.application.service.SelectNicknameService;
import com.letraaletra.api.application.usecase.UseCase;
import com.letraaletra.api.domain.security.PasswordService;
import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.EmailAlreadyInUseException;
import com.letraaletra.api.domain.user.factory.UserFactory;

import java.util.UUID;

public class CreateUserUseCase implements UseCase<CreateUserCommand, CreateUserOutput> {
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final UserFactory userFactory;
    private final SelectNicknameService selectNicknameService;

    public CreateUserUseCase(
            UserRepository userRepository,
            PasswordService passwordService,
            UserFactory userFactory,
            SelectNicknameService selectNicknameService
    ) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.userFactory = userFactory;
        this.selectNicknameService = selectNicknameService;
    }

    public CreateUserOutput execute(CreateUserCommand command) {

        String email = command.email();
        String password = command.password();

        validateEmail(email);

        String userId = UUID.randomUUID().toString();

        String nickname = selectNicknameService.execute();

        User user = userFactory.createLocal(userId, nickname, email, passwordService.hash(password));

        userRepository.save(user);

        return buildResult(user);
    }

    private void validateEmail(String email) {
        boolean existsEmail = userRepository.existsByEmail(email);

        if (existsEmail) {
            throw new EmailAlreadyInUseException();
        }
    }

    private CreateUserOutput buildResult(User user) {
        return new CreateUserOutput(
                user.getId(),
                user.getNickname(),
                user.getAvatar(),
                user.getEmail()
        );
    }
}
