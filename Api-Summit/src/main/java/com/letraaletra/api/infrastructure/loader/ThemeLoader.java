package com.letraaletra.api.infrastructure.loader;

import com.letraaletra.api.domain.game.board.theme.Theme;
import com.letraaletra.api.domain.game.board.theme.ThemeMessages;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ThemeLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Theme> load() {
        try (InputStream inputStream = getClass().getResourceAsStream("/data/themes.json")) {
            if (inputStream == null) {
                throw new RuntimeException("File /data/themes.json not found!");
            }

            List<ThemeJson> themes = objectMapper.readValue(
                    inputStream,
                    new TypeReference<  >() {
                    }
            );

            return themes.stream()
                    .collect(Collectors.toMap(ThemeJson::id, theme -> new Theme(theme.id(), theme.name(), theme.words())));

        } catch (Exception ex) {
            throw new RuntimeException(
                    ThemeMessages.FAILED_TO_LOAD_THEMES.getMessage(),
                    ex
            );
        }
    }
}