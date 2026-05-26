package com.letraaletra.api.infrastructure.loader;

import com.letraaletra.api.domain.repository.avatar.AvatarRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AvatarDatabaseSeeder implements CommandLineRunner {
    private final AvatarLoader loader;
    private final AvatarRepository repository;

    public AvatarDatabaseSeeder(AvatarLoader avatarLoader, AvatarRepository avatarRepository) {
        this.loader = avatarLoader;
        this.repository = avatarRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loader.load().values().forEach(repository::save);
    }
}
