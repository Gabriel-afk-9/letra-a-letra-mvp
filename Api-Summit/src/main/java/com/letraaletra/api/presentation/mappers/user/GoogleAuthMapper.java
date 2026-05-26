package com.letraaletra.api.presentation.mappers.user;

import com.letraaletra.api.application.command.auth.AuthCommand;
import com.letraaletra.api.presentation.dto.request.user.GoogleAuthRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class GoogleAuthMapper {
    public AuthCommand toCommand(GoogleAuthRequestDTO request) {
        return new AuthCommand(
                request.token()
        );
    }
}
