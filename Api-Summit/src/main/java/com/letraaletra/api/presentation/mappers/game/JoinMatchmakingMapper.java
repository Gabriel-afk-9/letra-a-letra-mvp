package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.application.command.game.JoinMatchmakingCommand;
import com.letraaletra.api.application.output.game.JoinMatchmakingOutput;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.state.GameMode;
import com.letraaletra.api.domain.game.matchmaking.MatchmakingUser;
import com.letraaletra.api.presentation.dto.response.game.MatchmakingStatus;
import com.letraaletra.api.presentation.dto.response.websocket.JoinMatchmakingResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JoinMatchmakingMapper {
    @Autowired
    private GameStateDTOMapper gameStateDTOMapper;

    public JoinMatchmakingCommand toCommand(String user, String session, GameMode gameMode) {
        MatchmakingUser matchmakingUser = new MatchmakingUser(user, session);

        return new JoinMatchmakingCommand(matchmakingUser, gameMode);
    }

    public JoinMatchmakingResponseDTO toResponseDTO(JoinMatchmakingOutput output) {
        Game game = output.game().orElse(null);
        String token = output.gameTokenId().orElse(null);

        return new JoinMatchmakingResponseDTO(
                game != null ? MatchmakingStatus.FOUNDED : MatchmakingStatus.SEARCHING,
                game != null ? game.getGameState().getCurrentTurnEnds() : null,
                token,
                game != null ? gameStateDTOMapper.toAllDTO(game) : null
        );
    }
}
