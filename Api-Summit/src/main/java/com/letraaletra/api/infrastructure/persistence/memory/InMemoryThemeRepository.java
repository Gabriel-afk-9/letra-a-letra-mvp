package com.letraaletra.api.infrastructure.persistence.memory;

import com.letraaletra.api.domain.repository.theme.ThemeRepository;
import com.letraaletra.api.domain.game.board.theme.Theme;
import com.letraaletra.api.infrastructure.loader.ThemeLoader;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryThemeRepository implements ThemeRepository {

    private final Map<String, Theme> themeMap;

    public InMemoryThemeRepository(ThemeLoader loader) {
        this.themeMap = new ConcurrentHashMap<>(Map.copyOf(loader.load()));
    }

    @Override
    public void save(Theme theme) {
        themeMap.put(theme.getThemeId(), theme);
    }

    @Override
    public Theme findById(String themeId) {
        return themeMap.get(themeId);
    }

    @Override
    public List<Theme> findAll() {
        return List.copyOf(themeMap.values());
    }
}
