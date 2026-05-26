package com.letraaletra.api.presentation.websocket.handlers.action;

import com.letraaletra.api.domain.game.player.actions.BlindPlayerAction;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.presentation.dto.request.player.BlindActionDTO;
import org.springframework.stereotype.Component;

@Component
public class BlindActionHandler extends AbstractPlayerActionHandler<BlindActionDTO> {

    @Override
    protected GameAction createAction(BlindActionDTO request) {
        return new BlindPlayerAction(
                request.actionId(),
                request.targetId()
        );
    }
    @Override
    public Class<BlindActionDTO> getType() {
        return BlindActionDTO.class;
    }
}
