package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.service.GameOverResult;
import com.letraaletra.api.presentation.dto.response.game.GameOverDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameOverResponseDTO;
import com.letraaletra.api.presentation.mappers.player.PlayerDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameOverMapper {
    @Autowired
    private PlayerDTOMapper playerDTOMapper;

    public GameOverResponseDTO toResponseDTO(GameOverResult gameOverResult, Game game) {
        Player winner = gameOverResult.winner();
        Player loser = gameOverResult.loser();

        return new GameOverResponseDTO(
                new GameOverDTO(
                        winner != null ? playerDTOMapper.toDTO(winner, game.getParticipantByUserId(winner.getUserId())) : null,
                        loser != null ? playerDTOMapper.toDTO(loser, game.getParticipantByUserId(loser.getUserId())) : null
                )
        );
    }
}
