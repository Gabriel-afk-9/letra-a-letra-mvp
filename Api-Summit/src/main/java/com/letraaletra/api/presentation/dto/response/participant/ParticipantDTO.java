package com.letraaletra.api.presentation.dto.response.participant;

import com.letraaletra.api.domain.game.participant.ParticipantRole;

public record ParticipantDTO(
        String id,
        String nickname,
        String avatar,
        ParticipantRole role
) {
}
