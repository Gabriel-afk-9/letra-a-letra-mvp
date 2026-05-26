package com.letraaletra.api.application.usecase.user;

import com.letraaletra.api.application.command.user.SetAvatarCommand;
import com.letraaletra.api.application.output.user.SetAvatarOutput;
import com.letraaletra.api.application.usecase.UseCase;
import com.letraaletra.api.domain.avatar.Avatar;
import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.InvalidUserAvatarSelectedException;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

import java.util.List;

public class SetAvatarUseCase implements UseCase<SetAvatarCommand, SetAvatarOutput> {
    private final UserRepository userRepository;

    public SetAvatarUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public SetAvatarOutput execute(SetAvatarCommand command) {
        User user = userRepository.find(command.userId()).orElse(null);
        validateUser(user);
        validateAvatar(command.avatar(), user);

        user.setAvatar(command.avatar());

        userRepository.save(user);

        return buildOutput(user.getAvatar());
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private void validateAvatar(String avatar, User user) {
        List<Avatar> userAvatars = userRepository.getAvatars(user.getId());

        if (userAvatars.stream().noneMatch(userAvatar -> userAvatar.avatarId().equals(avatar))) {
            throw new InvalidUserAvatarSelectedException();
        }
    }

    private SetAvatarOutput buildOutput(String avatar) {
        return new SetAvatarOutput(avatar);
    }
}
