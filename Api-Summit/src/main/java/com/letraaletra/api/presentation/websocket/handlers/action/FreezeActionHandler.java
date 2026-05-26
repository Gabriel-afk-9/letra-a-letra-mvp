package com.letraaletra.api.presentation.websocket.handlers.action;

import com.letraaletra.api.domain.game.player.actions.FreezePlayerAction;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.presentation.dto.request.player.FreezeActionDTO;
import org.springframework.stereotype.Component;

@Component
public class FreezeActionHandler extends AbstractPlayerActionHandler<FreezeActionDTO> {
    @Override
    protected GameAction createAction(FreezeActionDTO request) {
        return new FreezePlayerAction(
                request.actionId(),
                request.targetId()
        );
    }

    @Override
    public Class<FreezeActionDTO> getType() {
        return FreezeActionDTO.class;
    }
}
