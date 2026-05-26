package com.letraaletra.api.domain.game.service;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.state.GameMode;
import com.letraaletra.api.domain.game.state.GameState;
import com.letraaletra.api.domain.game.board.Board;
import com.letraaletra.api.domain.game.board.service.BoardGenerator;

import java.util.List;

public class DefaultGameStateGenerator {
    private final GameStateGenerator gameStateGenerator;
    private final BoardGenerator boardGenerator;

    public DefaultGameStateGenerator(GameStateGenerator gameStateGenerator, BoardGenerator boardGenerator) {
        this.gameStateGenerator = gameStateGenerator;
        this.boardGenerator = boardGenerator;
    }

    public GameState generate(Game game, GameMode gameMode, List<String> words) {
        Board board = boardGenerator.generate(words, gameMode);
        return gameStateGenerator.generate(game.getParticipants(), board);
    }
}
