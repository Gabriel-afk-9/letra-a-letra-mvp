package com.letraaletra.api.presentation.dto.response.game.board.cell;

public record CellView(
        boolean revealed,
        Character letter,
        String revealedBy,
        EffectView effect
) {
}
