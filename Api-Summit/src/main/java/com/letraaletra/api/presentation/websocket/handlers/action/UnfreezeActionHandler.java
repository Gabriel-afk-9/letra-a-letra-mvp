package com.letraaletra.api.presentation.websocket.handlers.action;

import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.domain.game.player.actions.UnfreezeAction;
import com.letraaletra.api.presentation.dto.request.player.UnfreezeActionDTO;
import org.springframework.stereotype.Component;

@Component
public class UnfreezeActionHandler extends AbstractPlayerActionHandler<UnfreezeActionDTO> {
    @Override
    protected GameAction createAction(UnfreezeActionDTO request) {
        return new UnfreezeAction(
                request.actionId()
        );
    }

    @Override
    public Class<UnfreezeActionDTO> getType() {
        return UnfreezeActionDTO.class;
    }
}
