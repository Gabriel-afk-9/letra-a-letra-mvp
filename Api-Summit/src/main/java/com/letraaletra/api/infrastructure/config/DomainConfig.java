package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.domain.game.board.service.BoardGenerator;
import com.letraaletra.api.domain.game.board.service.CellFactory;
import com.letraaletra.api.domain.game.service.DefaultGameGenerator;
import com.letraaletra.api.domain.game.service.DefaultGameStateGenerator;
import com.letraaletra.api.domain.game.service.GameStateGenerator;
import com.letraaletra.api.domain.game.service.GenerateRoomCode;
import com.letraaletra.api.domain.user.factory.UserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class DomainConfig {

    @Bean
    public BoardGenerator boardGenerator(CellFactory cellFactory) {
        return new BoardGenerator(cellFactory);
    }

    @Bean
    public CellFactory cellFactory(Random random) {
        return new CellFactory(random);
    }

    @Bean
    public GameStateGenerator gameStateGenerator() {
        return new GameStateGenerator();
    }

    @Bean
    public GenerateRoomCode generateRoomCode() {
        return new GenerateRoomCode();
    }

    @Bean
    public DefaultGameStateGenerator defaultGameStateGenerator(GameStateGenerator gameStateGenerator, BoardGenerator boardGenerator) {
        return new DefaultGameStateGenerator(gameStateGenerator, boardGenerator);
    }

    @Bean
    public DefaultGameGenerator defaultGameGenerator() {
        return new DefaultGameGenerator();
    }

    @Bean
    public UserFactory userFactory() {
        return new UserFactory();
    }
}
