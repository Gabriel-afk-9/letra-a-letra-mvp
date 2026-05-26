package com.letraaletra.api.presentation.mappers.player;

import com.letraaletra.api.application.command.player.PlayerActionCommand;
import com.letraaletra.api.application.output.player.PlayerActionOutput;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.presentation.dto.response.websocket.PlayerActionResponseDTO;
import com.letraaletra.api.presentation.mappers.game.GameStateDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerActionMapper {
    @Autowired
    private GameStateDTOMapper gameStateDTOMapper;

    public PlayerActionCommand toCommand(String token, String userId, GameAction action) {
        return new PlayerActionCommand(
                token,
                userId,
                action
        );
    }

    public PlayerActionResponseDTO toResponseDTO(PlayerActionOutput output, String viewer) {
        return new PlayerActionResponseDTO(
                output.game().getGameState().getCurrentTurnEnds(),
                output.events(),
                gameStateDTOMapper.toDTO(output.game(), viewer)
        );
    }

    public PlayerActionResponseDTO toAllResponseDTO(PlayerActionOutput output) {
        return new PlayerActionResponseDTO(
                output.game().getGameState().getCurrentTurnEnds(),
                output.events(),
                gameStateDTOMapper.toAllDTO(output.game())
        );
    }
}
