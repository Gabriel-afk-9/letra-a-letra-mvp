package com.letraaletra.api.presentation.mappers.player;

import com.letraaletra.api.domain.game.board.cell.PowerType;
import com.letraaletra.api.presentation.dto.response.player.InventoryDTO;
import org.springframework.stereotype.Component;

@Component
public class InventoryDTOMapper {
    public InventoryDTO toDTO(String id, PowerType power) {
        return new InventoryDTO(
                id,
                power.name()
        );
    }
}
