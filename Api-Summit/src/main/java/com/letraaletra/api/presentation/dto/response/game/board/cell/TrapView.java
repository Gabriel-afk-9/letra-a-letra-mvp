package com.letraaletra.api.presentation.dto.response.game.board.cell;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("TRAP")
public record TrapView(
        String ownerId
) implements EffectView {
}
