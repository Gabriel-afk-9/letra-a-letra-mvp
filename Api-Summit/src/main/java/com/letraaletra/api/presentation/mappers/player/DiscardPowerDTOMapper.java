package com.letraaletra.api.presentation.mappers.player;

import com.letraaletra.api.application.command.player.DiscardPowerCommand;
import com.letraaletra.api.application.output.player.DiscardPowerOutput;
import com.letraaletra.api.presentation.dto.request.DiscardPowerWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.DiscardPowerResponseDTO;
import com.letraaletra.api.presentation.mappers.game.GameStateDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiscardPowerDTOMapper {
    @Autowired
    private GameStateDTOMapper gameStateDTOMapper;

    public DiscardPowerCommand toCommand(DiscardPowerWsRequest request, String userId) {
        return new DiscardPowerCommand(
                request.tokenGameId(),
                userId,
                request.powerId()
        );
    }

    public DiscardPowerResponseDTO toResponseDTO(DiscardPowerOutput output, String viewer) {
        return new DiscardPowerResponseDTO(
                gameStateDTOMapper.toDTO(output.game(), viewer)
        );
    }
}
