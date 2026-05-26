package com.letraaletra.api.presentation.mappers.board;

import com.letraaletra.api.presentation.dto.response.game.board.cell.BlockView;
import com.letraaletra.api.domain.game.board.Board;
import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.board.cell.effect.BlockEffect;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.presentation.dto.response.game.board.BoardDTO;
import org.springframework.stereotype.Component;

@Component
public class BoardDTOMapper {
    public BoardDTO[][] toDTO(Board board) {
        int range = board.grid().length;

        BoardDTO[][] dto = new BoardDTO[range][range];

        for (int i = 0; i < range; i++) {
            for (int j = 0; j < range; j++) {
                Cell cell = board.getCell(new Position(i, j));

                dto[i][j] = mapCellToView(cell);
            }
        }

        return dto;
    }

    private BoardDTO mapCellToView(Cell cell) {
        if (cell.isRevealed()) {
            return new BoardDTO(
                    true,
                    cell.getLetter(),
                    cell.getRevealedById(),
                    null
            );
        }

        return new BoardDTO(
                false,
                null,
                null,
                cell.getEffect() instanceof BlockEffect block ?
                        new BlockView(
                                block.getOwnerId(),
                                block.getRemainingAttempts()
                        ) : null
        );
    }
}
