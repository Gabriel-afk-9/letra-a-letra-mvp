package com.letraaletra.api.presentation.websocket.handlers.action;

import com.letraaletra.api.application.command.player.PlayerActionCommand;
import com.letraaletra.api.application.output.player.PlayerActionOutput;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.application.usecase.player.PlayerActionUseCase;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.ParticipantRole;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.presentation.dto.request.player.PlayerActionDTO;
import com.letraaletra.api.presentation.dto.response.websocket.PlayerActionResponseDTO;
import com.letraaletra.api.presentation.mappers.player.PlayerActionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

public abstract class AbstractPlayerActionHandler<T extends PlayerActionDTO>
        implements InGameActionHandler<T> {

    @Autowired
    protected PlayerActionUseCase playerActionUseCase;

    @Autowired
    protected PlayerActionMapper playerActionMapper;

    @Autowired
    protected GameNotifier gameNotifier;

    @Override
    public void handle(T request, WebSocketSession session, String gameTokenId) {
        String userId = (String) session.getAttributes().get("userId");

        GameAction action = createAction(request);

        PlayerActionCommand command =
                playerActionMapper.toCommand(gameTokenId, userId, action);

        PlayerActionOutput output = playerActionUseCase.execute(command);

        send(output);

        afterHandle(output);
    }

    protected abstract GameAction createAction(T request);

    protected void afterHandle(PlayerActionOutput output) {
    }

    private void send(PlayerActionOutput output) {
        List<Player> players = output.game().getGameState()
                .getPlayers().values()
                .stream().toList();

        List<Participant> spectators = output.game().getParticipants().stream()
                .filter(participant -> participant.getRole().equals(ParticipantRole.SPECTATOR))
                .toList();

        for (Player player : players) {
            PlayerActionResponseDTO dto = playerActionMapper.toResponseDTO(output, player.getUserId());

            gameNotifier.notifierOne(player.getUserId(), dto);
        }

        for (Participant spectator : spectators) {
            PlayerActionResponseDTO dto = playerActionMapper.toAllResponseDTO(output);

            gameNotifier.notifierOne(spectator.getUserId(), dto);
        }
    }
}