package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.command.participant.SwapPositionCommand;
import com.letraaletra.api.application.output.participant.SwapPositionOutput;
import com.letraaletra.api.application.usecase.participant.SwapRoomPositionUseCase;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.presentation.dto.request.SwapPositionWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.SwapPositionResponseDTO;
import com.letraaletra.api.presentation.mappers.participant.SwapPositionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SwapRoomPlaceHandler implements RoomRequestHandler<SwapPositionWsRequest> {
    @Autowired
    private SwapRoomPositionUseCase swapRoomPosition;

    @Autowired
    private SwapPositionMapper swapPositionMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(SwapPositionWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        SwapPositionCommand command = swapPositionMapper.toCommand(request, userId);

        SwapPositionOutput output = swapRoomPosition.execute(command);

        SwapPositionResponseDTO dto = swapPositionMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);
    }

    @Override
    public Class<SwapPositionWsRequest> getType() {
        return SwapPositionWsRequest.class;
    }
}
