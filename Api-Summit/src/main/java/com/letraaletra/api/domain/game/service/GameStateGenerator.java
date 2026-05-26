package com.letraaletra.api.domain.game.service;

import com.letraaletra.api.domain.game.state.GameState;
import com.letraaletra.api.domain.game.board.Board;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.ParticipantRole;
import com.letraaletra.api.domain.game.player.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameStateGenerator {
    public GameState generate(List<Participant> participants, Board board) {
        Map<String, Player> players = new HashMap<>();

        participants
                .stream().filter(p -> p.getRole() == ParticipantRole.PLAYER)
                .forEach(p -> {
                    Player player = new Player(
                            p.getUserId()
                    );

                    players.put(p.getUserId(), player);
                });

        String matchId = UUID.randomUUID().toString();

        return new GameState(
                matchId,
                players,
                board,
                Instant.now().plusSeconds(45)
        );
    }
}
