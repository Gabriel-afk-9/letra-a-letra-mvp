package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.application.command.game.FindByTokenCommand;
import com.letraaletra.api.application.output.game.FindByTokenOutput;
import com.letraaletra.api.presentation.dto.response.http.FindByTokenResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindByTokenMapper {

    @Autowired
    private GameDTOMapper gameDTOMapper;

    public FindByTokenCommand toCommand(String token) {
        return new FindByTokenCommand(token);
    }

    public FindByTokenResponseDTO toResponseDTO(FindByTokenOutput output) {
        return new FindByTokenResponseDTO(
                gameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
