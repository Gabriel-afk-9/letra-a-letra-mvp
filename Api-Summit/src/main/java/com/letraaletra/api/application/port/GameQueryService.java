package com.letraaletra.api.application.port;

import com.letraaletra.api.domain.game.Game;

import java.util.List;

public interface GameQueryService {
    Game findByCode(String code);
    boolean existsByCode(String code);
    List<Game> getAllActiveGames();
    List<Game> getPublic();
}
