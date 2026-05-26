package com.letraaletra.api.presentation.mappers.player;

import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.presentation.dto.response.player.PlayerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerDTOMapper {
    @Autowired
    private InventoryDTOMapper inventoryDTOMapper;

    public PlayerDTO toDTO(Player player, Participant participant) {
        return new PlayerDTO(
                participant.getUserId(),
                participant.getNickname(),
                participant.getAvatar(),
                player.getScore(),
                player.getInventory().keySet().stream()
                        .map(key -> inventoryDTOMapper.toDTO(key, player.getInventory().get(key)))
                        .toList(),
                player.getEffects()
        );
    }
}
