package com.letraaletra.api.presentation.websocket.handlers.action;

import com.letraaletra.api.application.output.player.PlayerActionOutput;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.domain.game.player.actions.RevealCellAction;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.presentation.dto.request.player.RevealActionDTO;
import org.springframework.stereotype.Component;

@Component
public class RevealActionHandler extends AbstractPlayerActionHandler<RevealActionDTO> {

    @Override
    protected GameAction createAction(RevealActionDTO request) {
        return new RevealCellAction(
                new Position(request.position().x(), request.position().y())
        );
    }

    @Override
    protected void afterHandle(PlayerActionOutput output) {
        output.gameOver().ifPresent(
                gameOver -> gameNotifier.notifierGameOver(output.game(), gameOver)
        );
    }

    @Override
    public Class<RevealActionDTO> getType() {
        return RevealActionDTO.class;
    }
}