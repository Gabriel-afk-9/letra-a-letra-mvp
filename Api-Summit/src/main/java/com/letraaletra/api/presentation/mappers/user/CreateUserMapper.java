package com.letraaletra.api.presentation.mappers.user;

import com.letraaletra.api.application.command.user.CreateUserCommand;
import com.letraaletra.api.application.output.user.CreateUserOutput;
import com.letraaletra.api.presentation.dto.request.user.CreateUserRequestDTO;
import com.letraaletra.api.presentation.dto.response.user.CreateUserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CreateUserMapper {
    public CreateUserCommand toCommand(CreateUserRequestDTO dto) {
        return new CreateUserCommand(
                dto.email(),
                dto.password()
        );
    }

    public CreateUserResponseDTO toResponseDTO(CreateUserOutput output) {
        return new CreateUserResponseDTO(
              output.id(),
              output.avatar(),
              output.email()
        );
    }
}
