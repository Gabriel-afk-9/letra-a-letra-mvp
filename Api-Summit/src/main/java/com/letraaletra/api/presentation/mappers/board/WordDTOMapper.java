package com.letraaletra.api.presentation.mappers.board;

import com.letraaletra.api.domain.game.board.word.Word;
import com.letraaletra.api.presentation.dto.response.game.board.word.WordDTO;
import org.springframework.stereotype.Component;

@Component
public class WordDTOMapper {
    public WordDTO toDTO(Word word) {
        return new WordDTO(
                word.getValue(),
                word.isFound(),
                word.getFoundById()
        );
    }
}
