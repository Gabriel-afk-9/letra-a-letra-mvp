package com.letraaletra.api.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("UNBAN_PARTICIPANT")
public record UnbanParticipantWsRequest(
        @NotBlank
        String tokenGameId,

        @NotBlank
        String userId
) implements WsRequestDTO {
}
