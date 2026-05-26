package com.letraaletra.api.presentation.websocket.handlers.action;

import com.letraaletra.api.domain.game.player.actions.DetectTrapsAction;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.presentation.dto.request.player.DetectTrapsActionDTO;
import org.springframework.stereotype.Component;

@Component
public class DetectTrapsActionHandler extends AbstractPlayerActionHandler<DetectTrapsActionDTO> {

    @Override
    protected GameAction createAction(DetectTrapsActionDTO request) {
        return new DetectTrapsAction(
                request.actionId()
        );
    }

    @Override
    public Class<DetectTrapsActionDTO> getType() {
        return DetectTrapsActionDTO.class;
    }
}
