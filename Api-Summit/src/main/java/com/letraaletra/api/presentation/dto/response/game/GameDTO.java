package com.letraaletra.api.presentation.dto.response.game;

import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;

import java.util.List;
import java.util.Map;

public record GameDTO(
    String tokenGameId,
    String gameName,
    List<ParticipantDTO> participants,
    Map<Integer, String> positions
) {
}
