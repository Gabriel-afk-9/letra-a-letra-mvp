package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("PARTICIPANT_UNBANNED")
public record UnbanParticipantResponseDTO(

) implements WsResponseDTO {
}
