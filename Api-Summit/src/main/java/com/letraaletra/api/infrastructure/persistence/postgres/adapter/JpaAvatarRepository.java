package com.letraaletra.api.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.domain.avatar.Avatar;
import com.letraaletra.api.domain.repository.avatar.AvatarRepository;
import com.letraaletra.api.infrastructure.persistence.postgres.jpa.SpringDataAvatarRepository;
import com.letraaletra.api.infrastructure.persistence.postgres.mapper.AvatarMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JpaAvatarRepository implements AvatarRepository {
    private final SpringDataAvatarRepository repository;

    public JpaAvatarRepository(
        SpringDataAvatarRepository springDataAvatarRepository
    ) {
        this.repository = springDataAvatarRepository;
    }

    @Override
    public void save(Avatar avatar) {
        repository.save(AvatarMapper.toEntity(avatar));
    }
}
