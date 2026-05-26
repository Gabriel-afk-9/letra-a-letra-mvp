package com.letraaletra.api.presentation.dto.response.game.board;

import com.letraaletra.api.presentation.dto.response.game.board.cell.EffectView;

public record BoardDTO(
        boolean revealed,
        Character letter,
        String revealedBy,
        EffectView effect
) {
}
