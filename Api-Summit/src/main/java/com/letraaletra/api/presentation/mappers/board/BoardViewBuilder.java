package com.letraaletra.api.presentation.mappers.board;

import com.letraaletra.api.domain.game.state.GameState;
import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.board.cell.effect.BlockEffect;
import com.letraaletra.api.domain.game.board.cell.effect.TrapEffect;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.player.effect.BlindEffect;
import com.letraaletra.api.domain.game.player.effect.DetectTrapsEffect;
import com.letraaletra.api.domain.game.player.effect.SpyEffect;
import com.letraaletra.api.presentation.dto.response.game.board.BoardView;
import com.letraaletra.api.presentation.dto.response.game.board.cell.BlockView;
import com.letraaletra.api.presentation.dto.response.game.board.cell.CellView;
import com.letraaletra.api.presentation.dto.response.game.board.cell.TrapView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BoardViewBuilder {
    public BoardView build(GameState state, String viewerId) {

        Player player = state.getPlayerOrThrow(viewerId);

        Cell[][] grid = state.getBoard().grid();

        return new BoardView(getBoardView(grid, player));
    }

    private List<List<CellView>> getBoardView(Cell[][] grid, Player player) {
        List<List<CellView>> view = new ArrayList<>();

        boolean isBlind = player.getEffects()
                .stream().anyMatch(effect -> effect instanceof BlindEffect);

        boolean isDetectingTraps = player.getEffects()
                .stream().anyMatch(effect -> effect instanceof DetectTrapsEffect);

        List<SpyEffect> spies = player.getEffects()
                .stream()
                .flatMap(effect -> effect instanceof SpyEffect spy ? Stream.of(spy) : Stream.empty())
                .toList();

        Set<Position> spyPositions = spies.stream()
                .map(SpyEffect::getPosition)
                .collect(Collectors.toSet());

        for (Cell[] rowCells : grid) {
            List<CellView> row = getCellViews(rowCells, spyPositions, isBlind, isDetectingTraps, player.getUserId());

            view.add(row);
        }

        return view;
    }

    private List<CellView> getCellViews(
            Cell[] rowCells,
            Set<Position> spyPositions,
            boolean isBlind,
            boolean isDetectingTraps,
            String playerId
    ) {
        List<CellView> row = new ArrayList<>(rowCells.length);

        for (Cell cell : rowCells) {
            row.add(mapCell(cell, spyPositions, isBlind, isDetectingTraps, playerId));
        }

        return row;
    }

    private CellView mapCell(
            Cell cell,
            Set<Position> spyPositions,
            boolean isBlind,
            boolean isDetectingTraps,
            String playerId
    ) {
        if (cell.getEffect() instanceof BlockEffect block) {
            return new CellView(
                    false,
                    null,
                    null,
                    new BlockView(
                            block.getOwnerId(),
                            block.getRemainingAttempts()
                    )
            );
        }

        if (cell.getEffect() instanceof TrapEffect trap) {
            boolean visible =
                    isDetectingTraps || trap.getOwnerId().equals(playerId);

            return new CellView(
                    false,
                    null,
                    null,
                    visible ? new TrapView(trap.getOwnerId()) : null
            );
        }

        boolean revealed = cell.isRevealed();

        if (spyPositions.contains(cell.getPosition()) && !revealed) {
            return new CellView(false, cell.getLetter(), null, null);
        }

        if (isBlind) {
            return new CellView(
                    revealed,
                    null,
                    null,
                    null
            );
        }

        return new CellView(
                revealed,
                revealed ? cell.getLetter() : null,
                revealed ? cell.getRevealedById() : null,
                null
        );
    }
}
