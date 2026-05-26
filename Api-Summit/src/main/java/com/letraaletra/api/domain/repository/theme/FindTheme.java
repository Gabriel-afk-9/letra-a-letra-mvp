package com.letraaletra.api.domain.repository.theme;

import com.letraaletra.api.domain.game.board.theme.Theme;

import java.util.List;

public interface FindTheme {
    Theme findById(String themeId);
    List<Theme> findAll();
}
