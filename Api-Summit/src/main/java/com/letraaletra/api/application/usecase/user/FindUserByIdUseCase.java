package com.letraaletra.api.application.usecase.user;

import com.letraaletra.api.application.command.user.FindUserCommand;
import com.letraaletra.api.application.output.user.FindUserOutput;
import com.letraaletra.api.application.usecase.UseCase;
import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

public class FindUserByIdUseCase implements UseCase<FindUserCommand, FindUserOutput> {
    private final UserRepository userRepository;

    public FindUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public FindUserOutput execute(FindUserCommand command) {
        User user = userRepository.find(command.id()).orElse(null);
        validateUser(user);

        return buildReturn(user);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private FindUserOutput buildReturn(User user) {
        return new FindUserOutput(
                user.getId(),
                user.getNickname(),
                user.getAvatar(),
                user.getEmail()
        );
    }
}
