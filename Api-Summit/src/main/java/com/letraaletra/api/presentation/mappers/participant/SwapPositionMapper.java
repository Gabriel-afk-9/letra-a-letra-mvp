package com.letraaletra.api.presentation.mappers.participant;

import com.letraaletra.api.application.command.participant.SwapPositionCommand;
import com.letraaletra.api.application.output.participant.SwapPositionOutput;
import com.letraaletra.api.presentation.dto.request.SwapPositionWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.SwapPositionResponseDTO;
import com.letraaletra.api.presentation.mappers.game.GameDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SwapPositionMapper {
    @Autowired
    private GameDTOMapper gameDTOMapper;

    public SwapPositionCommand toCommand(SwapPositionWsRequest request, String userId) {
        return new SwapPositionCommand(
                request.tokenGameId(),
                request.position(),
                userId
        );
    }

    public SwapPositionResponseDTO toResponseDTO(SwapPositionOutput output) {
        return new SwapPositionResponseDTO(
                gameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
