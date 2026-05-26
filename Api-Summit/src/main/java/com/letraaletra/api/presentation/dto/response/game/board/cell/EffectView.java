package com.letraaletra.api.presentation.dto.response.game.board.cell;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "effect"
)
public sealed interface EffectView permits
        BlockView,
        TrapView
{}