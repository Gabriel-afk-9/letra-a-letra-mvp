package com.letraaletra.api.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("DISCARD_POWER")
public record DiscardPowerWsRequest(
        String tokenGameId,
        String powerId
) implements WsRequestDTO {
}
