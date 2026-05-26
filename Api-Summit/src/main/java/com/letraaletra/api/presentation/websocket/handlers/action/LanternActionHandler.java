package com.letraaletra.api.presentation.websocket.handlers.action;

import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.domain.game.player.actions.LanternAction;
import com.letraaletra.api.presentation.dto.request.player.LanternActionDTO;
import org.springframework.stereotype.Component;

@Component
public class LanternActionHandler extends AbstractPlayerActionHandler<LanternActionDTO> {
    @Override
    protected GameAction createAction(LanternActionDTO request) {
        return new LanternAction(
                request.actionId()
        );
    }

    @Override
    public Class<LanternActionDTO> getType() {
        return LanternActionDTO.class;
    }
}
