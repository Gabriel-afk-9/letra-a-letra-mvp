package com.letraaletra.api.presentation.dto.response.game.board.cell;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("BLOCK")
public record BlockView(
    String ownerId,
    Integer remainingClicks
) implements EffectView {
}
