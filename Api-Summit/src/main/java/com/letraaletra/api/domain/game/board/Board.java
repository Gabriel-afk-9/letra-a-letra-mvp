package com.letraaletra.api.domain.game.board;

import com.letraaletra.api.domain.game.state.GameMode;
import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.board.cell.effect.TrapEffect;
import com.letraaletra.api.domain.game.board.exception.InvalidCellPositionException;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.board.word.Word;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record Board(
        Cell[][] grid,
        Word[] words,
        GameMode gameMode
) {

    public Cell getCell(Position position) {
        int row = position.x();
        int col = position.y();

        if (row < 0 || row >= grid.length || col < 0 || col >= grid.length) {
            throw new InvalidCellPositionException();
        }

        return grid[row][col];
    }

    @Override
    public Word[] words() {
        return words.clone();
    }

    public List<Position> getOpponentTraps(String userId) {
        return Arrays.stream(grid)
                .flatMap(Arrays::stream)
                .filter(cell -> cell.getEffect() instanceof TrapEffect)
                .filter(cell -> !Objects.equals(cell.getEffect().getOwnerId(), userId))
                .map(Cell::getPosition)
                .toList();
    }
}
