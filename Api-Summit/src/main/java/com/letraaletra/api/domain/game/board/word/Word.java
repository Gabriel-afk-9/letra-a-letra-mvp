package com.letraaletra.api.domain.game.board.word;

import com.letraaletra.api.domain.game.board.position.Position;

import java.util.List;

public class Word {
    private final String value;
    private final List<Position> positions;

    private boolean found;
    private String foundById;

    public Word(String value, List<Position> positions) {
        this.value = value;
        this.positions = List.copyOf(positions);
        this.found = false;
    }

    public String getValue() {
        return value;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public boolean isFound() {
        return found;
    }

    public String getFoundById() {
        return foundById;
    }

    public boolean markAsFound(String playerId) {
        if (found) {
            return false;
        }

        this.found = true;
        this.foundById = playerId;
        return true;
    }
}
