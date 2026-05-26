package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.presentation.mappers.participant.MapParticipantsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameDTOMapper {
    @Autowired
    private MapParticipantsMapper mapParticipantsMapper;

    public GameDTO toDTO(Game game, String tokenGameId) {
        return new GameDTO(
                tokenGameId,
                game.getRoomName(),
                mapParticipantsMapper.execute(game),
                game.getPositions()
        );
    }
}
