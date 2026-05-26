package com.letraaletra.api.domain.game.player.actions;

import com.letraaletra.api.domain.game.event.CellRevealedEvent;
import com.letraaletra.api.domain.game.event.Event;
import com.letraaletra.api.domain.game.event.WordFoundedEvent;
import com.letraaletra.api.domain.game.state.GameState;
import com.letraaletra.api.domain.game.event.StateEvent;
import com.letraaletra.api.domain.game.board.Board;
import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.board.cell.PowerType;
import com.letraaletra.api.domain.game.board.cell.effect.CellEffect;
import com.letraaletra.api.domain.game.board.cell.effect.InteractResult;
import com.letraaletra.api.domain.game.board.word.Word;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.player.exception.NotYourTurnException;

import java.util.*;

public class RevealCellAction implements GameAction {
    private final Position position;

    public RevealCellAction(Position position) {
        this.position = position;
    }

    @Override
    public List<Event> execute(GameState state, String userId) {
        validatePlayerTurn(state, userId);

        Cell cell = state.getBoard().getCell(position);

        List<Event> events = new ArrayList<>();

        Player player = state.getPlayerOrThrow(userId);

        player.resetPassedTurn();

        boolean canContinue = activateEffect(cell, player.getUserId(), events);

        if (!canContinue) return events;

        PowerType drop = cell.reveal(userId);
        addPower(drop, player);

        List<Event> found = checkCompletedWords(cell, userId, state);

        events.add(new Event(
                StateEvent.CELL_REVEALED,
                new CellRevealedEvent(
                        position,
                        userId
                )
        ));

        if (found != null) events.addAll(found);

        return events;
    }

    private List<Event> checkCompletedWords(Cell cell, String userId, GameState state) {
        List<Word> words = cell.getRelatedWords();

        if (words.isEmpty()) return null;

        Board board = state.getBoard();

        List<Event> wordsFoundEvent = new ArrayList<>();

        for (Word word : words) {
            List<Position> positions = word.getPositions();

            boolean isComplete = positions.stream().allMatch(pos -> board.getCell(pos).isRevealed());

            if (isComplete && word.markAsFound(userId)) {
                Player player = state.getPlayerOrThrow(userId);
                player.incrementScore();
                wordsFoundEvent.add(new Event(
                        StateEvent.WORD_FOUNDED,
                        new WordFoundedEvent(
                                positions.toArray(Position[]::new),
                                userId
                        )
                ));
            }
        }

        return !wordsFoundEvent.isEmpty() ? wordsFoundEvent : null;
    }

    private void validatePlayerTurn(GameState state, String userId) {
        if (!state.currentPlayerTurn().equals(userId)) {
            throw new NotYourTurnException();
        }
    }

    private void addPower(PowerType drop, Player player) {
        if (drop == null) return;

        player.addToInventory(drop);
    }

    private boolean activateEffect(Cell cell, String player, List<Event> events) {
        if (cell.hasEffect()) {
            CellEffect effect = cell.getEffect();
            InteractResult result = effect.onInteract(this, player, cell);

            events.add(result.event());

            return result.canContinue();
        }

        return true;
    }
}
