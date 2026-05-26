package com.letraaletra.api.presentation.websocket.handlers.action;

import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.domain.game.player.actions.SpyCellAction;
import com.letraaletra.api.presentation.dto.request.player.SpyActionDTO;
import org.springframework.stereotype.Component;

@Component
public class SpyActionHandler extends AbstractPlayerActionHandler<SpyActionDTO> {

    @Override
    protected GameAction createAction(SpyActionDTO request) {
        return new SpyCellAction(
                request.actionId(),
                new Position(request.position().x(), request.position().y())
        );
    }

    @Override
    public Class<SpyActionDTO> getType() {
        return SpyActionDTO.class;
    }
}