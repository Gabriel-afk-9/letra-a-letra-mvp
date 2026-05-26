package com.letraaletra.api.presentation.dto.response.player;

import com.letraaletra.api.domain.game.player.effect.PlayerEffect;

import java.util.List;

public record PlayerDTO(
        String id,
        String nickname,
        String avatar,
        int score,
        List<InventoryDTO> inventory,
        List<PlayerEffect> effects
) {
}
