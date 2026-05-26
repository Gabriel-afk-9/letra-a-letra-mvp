package com.letraaletra.api.presentation.mappers.board;

import com.letraaletra.api.presentation.dto.response.game.board.BoardView;
import com.letraaletra.api.presentation.dto.response.game.board.cell.CellView;
import com.letraaletra.api.presentation.dto.response.game.board.BoardDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BoardViewDTOMapper {
    public BoardDTO[][] toDTO(BoardView view) {
        int size = view.grid().size();

        BoardDTO[][] dto = new BoardDTO[size][size];

        for (int i = 0; i < size; i++) {
            List<CellView> row = view.grid().get(i);

            for (int j = 0; j < row.size(); j++) {
                CellView cell = row.get(j);

                dto[i][j] = new BoardDTO(
                        cell.revealed(),
                        cell.letter(),
                        cell.revealedBy(),
                        cell.effect()
                );
            }
        }

        return dto;
    }
}