package com.letraaletra.api.presentation.websocket.handlers.action;

import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.domain.game.player.actions.ImmunityPlayerAction;
import com.letraaletra.api.presentation.dto.request.player.ImmunityActionDTO;
import org.springframework.stereotype.Component;

@Component
public class ImmunityActionHandler extends AbstractPlayerActionHandler<ImmunityActionDTO> {
    @Override
    protected GameAction createAction(ImmunityActionDTO request) {
        return new ImmunityPlayerAction(
                request.actionId()
        );
    }

    @Override
    public Class<ImmunityActionDTO> getType() {
        return ImmunityActionDTO.class;
    }
}
