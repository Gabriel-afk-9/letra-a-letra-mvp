package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.presentation.dto.response.game.board.BoardView;
import com.letraaletra.api.presentation.mappers.board.BoardViewBuilder;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;
import com.letraaletra.api.presentation.mappers.board.BoardDTOMapper;
import com.letraaletra.api.presentation.mappers.board.BoardViewDTOMapper;
import com.letraaletra.api.presentation.mappers.board.WordDTOMapper;
import com.letraaletra.api.presentation.mappers.player.PlayerDTOMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class GameStateDTOMapper {
    private final BoardViewBuilder boardViewBuilder;
    private final BoardViewDTOMapper boardViewDTOMapper;
    private final BoardDTOMapper boardDTOMapper;
    private final WordDTOMapper wordDTOMapper;
    private final PlayerDTOMapper playerDTOMapper;

    public GameStateDTOMapper(
            BoardViewBuilder boardViewBuilder,
            BoardViewDTOMapper boardViewDTOMapper,
            BoardDTOMapper boardDTOMapper,
            WordDTOMapper wordDTOMapper,
            PlayerDTOMapper playerDTOMapper
    ) {
        this.boardViewBuilder = boardViewBuilder;
        this.boardViewDTOMapper = boardViewDTOMapper;
        this.boardDTOMapper = boardDTOMapper;
        this.playerDTOMapper = playerDTOMapper;
        this.wordDTOMapper = wordDTOMapper;
    }

    public GameStateDTO toDTO(Game game, String viewerId) {
        BoardView boardView = boardViewBuilder.build(
                game.getGameState(),
                viewerId
        );

        return new GameStateDTO(
                game.getGameState().getPlayers().values().stream()
                        .map(player -> playerDTOMapper.toDTO(
                                player,
                                game.getParticipantByUserId(player.getUserId())
                        ))
                        .toList(),
                boardViewDTOMapper.toDTO(boardView),
                Arrays.stream(game.getGameState().getBoard().words()).map(wordDTOMapper::toDTO).toList(),
                game.getGameState().currentPlayerTurn()
        );
    }

    public GameStateDTO toAllDTO(Game game) {
        return new GameStateDTO(
                game.getGameState().getPlayers().values().stream()
                        .map(player -> playerDTOMapper.toDTO(
                                player,
                                game.getParticipantByUserId(player.getUserId())
                        ))
                        .toList(),
                boardDTOMapper.toDTO(game.getGameState().getBoard()),
                Arrays.stream(game.getGameState().getBoard().words()).map(wordDTOMapper::toDTO).toList(),
                game.getGameState().currentPlayerTurn()
        );
    }
}
