package com.letraaletra.api.presentation.mappers.participant;

import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;
import org.springframework.stereotype.Component;

@Component
public class ParticipantDTOMapper {
    public ParticipantDTO toDTO(Participant participant) {
        return new ParticipantDTO(
                participant.getUserId(),
                participant.getNickname(),
                participant.getAvatar(),
                participant.getRole()
        );
    }
}
