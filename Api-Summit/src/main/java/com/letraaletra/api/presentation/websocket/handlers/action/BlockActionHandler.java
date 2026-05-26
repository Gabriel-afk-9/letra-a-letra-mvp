package com.letraaletra.api.presentation.websocket.handlers.action;

import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.player.actions.BlockCellAction;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.presentation.dto.request.player.BlockActionDTO;
import org.springframework.stereotype.Component;

@Component
public class BlockActionHandler extends AbstractPlayerActionHandler<BlockActionDTO> {

    @Override
    protected GameAction createAction(BlockActionDTO request) {
        Position position = new Position(request.position().x(), request.position().y());

        return new BlockCellAction(
                request.actionId(),
                position
        );
    }

    @Override
    public Class<BlockActionDTO> getType() {
        return BlockActionDTO.class;
    }
}
