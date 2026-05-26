package com.letraaletra.api.presentation.mappers.user;

import com.letraaletra.api.application.command.user.FindUserCommand;
import com.letraaletra.api.application.output.user.FindUserOutput;
import com.letraaletra.api.presentation.dto.response.user.FindUserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class FindUserMapper {
    public FindUserCommand toCommand(String id) {
        return new FindUserCommand(id);
    }

    public FindUserResponseDTO toResponseDTO(FindUserOutput output) {
        return new FindUserResponseDTO(
                output.id(),
                output.nickname(),
                output.avatar(),
                output.email()
        );
    }
}
