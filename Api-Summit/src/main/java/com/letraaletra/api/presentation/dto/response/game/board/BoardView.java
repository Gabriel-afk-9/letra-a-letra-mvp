package com.letraaletra.api.presentation.dto.response.game.board;

import com.letraaletra.api.presentation.dto.response.game.board.cell.CellView;

import java.util.List;

public record BoardView(
        List<List<CellView>> grid
) {
}
