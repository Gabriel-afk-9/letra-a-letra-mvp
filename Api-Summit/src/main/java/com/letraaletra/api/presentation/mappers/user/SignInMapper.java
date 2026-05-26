package com.letraaletra.api.presentation.mappers.user;

import com.letraaletra.api.application.command.user.SignInCommand;
import com.letraaletra.api.application.output.user.SignInOutput;
import com.letraaletra.api.presentation.dto.request.user.SignInRequestDTO;
import com.letraaletra.api.presentation.dto.response.user.SignInResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class SignInMapper {
    public SignInCommand toCommand(SignInRequestDTO dto) {
        return new SignInCommand(
                dto.email(),
                dto.password()
        );
    }

    public SignInResponseDTO toResponseDTO(SignInOutput output) {
        return new SignInResponseDTO(
                output.id(),
                output.token()
        );
    }
}
