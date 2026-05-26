package com.letraaletra.api.presentation.dto.response.game.board.word;

public record WordDTO(
        String word,
        boolean found,
        String foundById
) {
}
