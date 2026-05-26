package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.command.player.DiscardPowerCommand;
import com.letraaletra.api.application.output.player.DiscardPowerOutput;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.application.usecase.player.DiscardPowerUseCase;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.presentation.dto.request.DiscardPowerWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.DiscardPowerResponseDTO;
import com.letraaletra.api.presentation.mappers.player.DiscardPowerDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Component
public class DiscardPowerHandler implements RoomRequestHandler<DiscardPowerWsRequest> {
    @Autowired
    private DiscardPowerUseCase discardPowerUseCase;

    @Autowired
    private DiscardPowerDTOMapper discardPowerDTOMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(DiscardPowerWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        DiscardPowerCommand command = discardPowerDTOMapper.toCommand(request, userId);

        DiscardPowerOutput output = discardPowerUseCase.execute(command);

        send(output);
    }

    private void send(DiscardPowerOutput output) {
        List<Player> players = output.game().getGameState()
                .getPlayers().values()
                .stream().toList();

        for (Player player : players) {
            DiscardPowerResponseDTO dto = discardPowerDTOMapper.toResponseDTO(output, player.getUserId());

            gameNotifier.notifierOne(player.getUserId(), dto);
        }
    }

    @Override
    public Class<DiscardPowerWsRequest> getType() {
        return DiscardPowerWsRequest.class;
    }
}
