package com.letraaletra.api.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("KICK_PARTICIPANT")
public record KickParticipantWsRequest(
        @NotBlank
        String tokenGameId,

        @NotBlank
        String participantId
) implements WsRequestDTO {
}
