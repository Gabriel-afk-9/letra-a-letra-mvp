package com.letraaletra.api.domain.game.board.theme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Theme {
    private final String id;
    private final String name;
    private final List<String> words;

    public Theme(String id, String name, List<String> words) {
        this.id = id;
        this.name = name;
        this.words = List.copyOf(words);
    }

    public String getThemeId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getWords() {
        return words;
    }

    public List<String> pickRandomWords(int amount, Random random) {
        List<String> shuffled = new ArrayList<>(this.words);
        Collections.shuffle(shuffled, random);

        return shuffled.stream()
                .limit(amount)
                .toList();
    }
}
