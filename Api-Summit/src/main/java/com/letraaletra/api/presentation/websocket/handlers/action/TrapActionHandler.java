package com.letraaletra.api.presentation.websocket.handlers.action;

import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.domain.game.player.actions.TrapCellAction;
import com.letraaletra.api.presentation.dto.request.player.TrapActionDTO;
import org.springframework.stereotype.Component;

@Component
public class TrapActionHandler extends AbstractPlayerActionHandler<TrapActionDTO> {
    @Override
    protected GameAction createAction(TrapActionDTO request) {
        Position position = new Position(request.position().x(), request.position().y());

        return new TrapCellAction(
                request.actionId(),
                position
        );
    }

    @Override
    public Class<TrapActionDTO> getType() {
        return TrapActionDTO.class;
    }
}
