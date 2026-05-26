package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.application.command.game.FindByCodeCommand;
import com.letraaletra.api.application.output.game.FindByCodeOutput;
import com.letraaletra.api.presentation.dto.response.http.FindByCodeResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class FindByCodeMapper {

    public FindByCodeCommand toCommand(String code) {
        return new FindByCodeCommand(code);
    }

    public FindByCodeResponseDTO toResponseDTO(FindByCodeOutput output) {
        return new FindByCodeResponseDTO(
                output.token()
        );
    }
}
