package com.letraaletra.api.presentation.mappers.participant;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MapParticipantsMapper {
    @Autowired
    private ParticipantDTOMapper participantDTOMapper;

    public List<ParticipantDTO> execute(Game game) {
        return game.getParticipants().stream()
                .map(participantDTOMapper::toDTO)
                .toList();
    }
}
