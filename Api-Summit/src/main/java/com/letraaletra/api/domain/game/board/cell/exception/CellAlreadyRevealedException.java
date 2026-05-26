package com.letraaletra.api.domain.game.board.cell.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class CellAlreadyRevealedException extends DomainException {
    public CellAlreadyRevealedException() {
        super(GameMessages.CELL_ALREADY_REVEALED);
    }
}
