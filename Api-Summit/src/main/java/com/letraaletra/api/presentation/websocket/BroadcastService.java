package com.letraaletra.api.presentation.websocket;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.application.port.SessionRepository;
import com.letraaletra.api.domain.game.service.GameOverResult;
import com.letraaletra.api.presentation.dto.response.websocket.GameOverResponseDTO;
import com.letraaletra.api.presentation.mappers.game.GameOverMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class BroadcastService implements GameNotifier {
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GameOverMapper gameOverMapper;

    private final Logger logger = LoggerFactory.getLogger(BroadcastService.class);

    @Override
    public void notifierAll(Game game, Object dto) {
        if (game == null) {
            throw new GameNotFoundException();
        }

        final String json;
        try {
            json = objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            logger.warn("Error serializing broadcast message: {}", e.getMessage());
            return;
        }

        for (Participant participant : game.getParticipants()) {
            WebSocketSession session = sessionRepository.find(participant.getSocketId());

            if (session == null || !session.isOpen()) {
                continue;
            }

            send(session, json);
        }
    }

    @Override
    public void notifierOne(String userId, Object dto) {
        WebSocketSession session = sessionRepository.findByUserId(userId);

        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(dto);
            send(session, json);
        } catch (Exception e) {
            logger.warn("Error serializing message for user {}: {}", userId, e.getMessage());
        }
    }

    @Override
    public void notifierGameOver(Game game, GameOverResult gameOverResult) {
        GameOverResponseDTO dto = gameOverMapper.toResponseDTO(gameOverResult, game);

        notifierAll(game, dto);
    }

    private void send(WebSocketSession session, String json) {
        if (session == null || !session.isOpen()) return;

        try {
            session.sendMessage(new TextMessage(json));

        } catch (IOException | IllegalStateException e) {
            logger.warn("Error sending message to {}: {}",
                    session.getId(),
                    e.getMessage()
            );
        }
    }
}
