package com.letraaletra.api.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("BAN_PARTICIPANT")
public record BanParticipantWsRequest(
        @NotBlank
        String tokenGameId,

        @NotBlank
        String participantId
) implements WsRequestDTO {
}
