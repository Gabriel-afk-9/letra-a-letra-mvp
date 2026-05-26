package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.domain.game.board.theme.Theme;
import com.letraaletra.api.domain.repository.theme.ThemeRepository;

import java.util.List;
import java.util.Random;

public class PickRandomThemeWordsUseCase {
    private final ThemeRepository themeRepository;
    private final Random random;

    public PickRandomThemeWordsUseCase(ThemeRepository themeRepository, Random random) {
        this.themeRepository = themeRepository;
        this.random = random;
    }

    public List<String> execute() {
        List<Theme> themes = themeRepository.findAll();

        Theme randomTheme = themes.get(random.nextInt(themes.size()));

        return randomTheme.pickRandomWords(5, random);
    }
}