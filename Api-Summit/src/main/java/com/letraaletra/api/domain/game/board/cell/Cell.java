package com.letraaletra.api.domain.game.board.cell;

import com.letraaletra.api.domain.game.board.cell.effect.CellEffect;
import com.letraaletra.api.domain.game.board.cell.exception.CellAlreadyRevealedException;
import com.letraaletra.api.domain.game.board.word.Word;
import com.letraaletra.api.domain.game.board.position.Position;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final char letter;
    private final Position position;
    private boolean revealed;
    private String revealedById;
    private final List<Word> relatedWords = new ArrayList<>();
    private PowerType drop;

    private CellEffect effect;

    public Cell(char letter, Position position, PowerType drop) {
        this.letter = letter;
        this.position = position;
        this.revealed = false;
        this.drop = drop;
    }

    public char getLetter() {
        return letter;
    }

    public Position getPosition() {
        return position;
    }

    public CellEffect getEffect() {
        return effect;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public String getRevealedById() {
        return revealedById;
    }

    public PowerType reveal(String actor) {
        if (revealed) {
            throw new CellAlreadyRevealedException();
        }

        this.revealed = true;
        this.revealedById = actor;
        PowerType drop = this.drop;
        this.drop = null;

        return drop;
    }

    public void addRelatedWord(Word word) {
        relatedWords.add(word);
    }

    public List<Word> getRelatedWords() {
        return List.copyOf(relatedWords);
    }

    public void setEffect(CellEffect effect) {
        this.effect = effect;
    }

    public void clearEffect() {
        this.effect = null;
    }

    public boolean hasEffect() {
        return effect != null;
    }
}
