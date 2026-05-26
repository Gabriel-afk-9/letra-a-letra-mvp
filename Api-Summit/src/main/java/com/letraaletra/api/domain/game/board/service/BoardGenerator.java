package com.letraaletra.api.domain.game.board.service;

import com.letraaletra.api.domain.game.board.Board;
import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.board.word.Word;
import com.letraaletra.api.domain.game.state.GameMode;
import com.letraaletra.api.domain.game.board.position.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BoardGenerator {
    private final CellFactory cellFactory;

    public BoardGenerator(CellFactory cellFactory) {
        this.cellFactory = cellFactory;
    }

    public Board generate(List<String> words, GameMode gameMode) {
        Cell[][] grid = new Cell[10][10];
        List<Word> placedWords = new ArrayList<>();

        for (String wordValue : words) {
            boolean placed = false;
            int tries = 0;

            while (!placed && tries < 500) {
                tries++;

                int row = ThreadLocalRandom.current().nextInt(grid.length);
                int column = ThreadLocalRandom.current().nextInt(grid[0].length);

                int[][] dirSet = gameMode.getDirections();
                int[] direction = dirSet[ThreadLocalRandom.current().nextInt(dirSet.length)];
                int dx = direction[0];
                int dy = direction[1];

                if (canPlaceWord(wordValue, row, column, dx, dy, grid)) {
                    Word placedWord = placeWord(wordValue, row, column, dx, dy, grid, gameMode);
                    placed = true;
                    placedWords.add(placedWord);
                }
            }
        }

        fillEmptySpaces(grid, gameMode);
        bindCellsToWords(grid, placedWords);

        return new Board(grid, placedWords.toArray(new Word[0]), gameMode);
    }

    private boolean canPlaceWord(String word, int row, int column, int dx, int dy, Cell[][] grid) {
        for (int i = 0; i < word.length(); i++) {
            int x = row + dx * i;
            int y = column + dy * i;

            if (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length) {
                return false;
            }

            if (grid[x][y] != null && grid[x][y].getLetter() != word.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    private Word placeWord(String word, int row, int column, int dx, int dy, Cell[][] grid, GameMode gameMode) {
        List<Position> positions = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            int x = row + dx * i;
            int y = column + dy * i;

            Position position = new Position(x, y);

            if (grid[x][y] == null) {
                grid[x][y] = cellFactory.create(word.charAt(i), position, gameMode);
            }

            positions.add(position);
        }

        return new Word(word, positions);
    }

    private void fillEmptySpaces(Cell[][] grid, GameMode gameMode) {
        String letters = "abcdefghijklmnopqrstuvwxyz";

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == null) {
                    int letterIdx = ThreadLocalRandom.current().nextInt(letters.length());
                    char letter = letters.charAt(letterIdx);

                    Position position = new Position(i, j);

                    grid[i][j] = cellFactory.create(letter, position, gameMode);
                }
            }
        }
    }

    private void bindCellsToWords(Cell[][] grid, List<Word> words) {
        for (Word word : words) {
            for (Position pos : word.getPositions()) {
                Cell cell = grid[pos.x()][pos.y()];
                cell.addRelatedWord(word);
            }
        }
    }
}