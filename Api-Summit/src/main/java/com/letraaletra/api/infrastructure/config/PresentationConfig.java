package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.presentation.mappers.board.BoardViewBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PresentationConfig {
    @Bean
    public BoardViewBuilder boardViewBuilder() {
        return new BoardViewBuilder();
    }
}
