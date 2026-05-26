package com.letraaletra.api.infrastructure.loader;

import com.letraaletra.api.domain.avatar.Avatar;
import com.letraaletra.api.domain.avatar.AvatarMessages;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AvatarLoader {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Avatar> load() {
        try (InputStream inputStream = getClass().getResourceAsStream("/data/avatar.json")) {
            if (inputStream == null) {
                throw new RuntimeException("File /data/themes.json not found!");
            }

            List<AvatarJson> avatars = objectMapper.readValue(
                    inputStream,
                    new TypeReference<>() {
                    }
            );

            return avatars.stream()
                    .collect(Collectors.toMap(AvatarJson::avatar, avatar -> new Avatar(avatar.avatar())));
        } catch (Exception ex) {
            throw new RuntimeException(
                    AvatarMessages.FAILED_TO_LOAD_AVATARS.getMessage(),
                    ex
            );
        }
    }
}
