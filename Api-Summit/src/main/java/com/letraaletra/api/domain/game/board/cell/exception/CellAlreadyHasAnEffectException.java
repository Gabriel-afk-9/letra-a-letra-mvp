package com.letraaletra.api.domain.game.board.cell.exception;

import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class CellAlreadyHasAnEffectException extends DomainException {
    public CellAlreadyHasAnEffectException() {
        super(GameMessages.CELL_ALREADY_HAS_AN_EFFECT);
    }
}
