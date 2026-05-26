package com.letraaletra.api.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("LEFT_GAME")
public record LeftGameWsRequest(
        @NotBlank
        String tokenGameId
) implements WsRequestDTO {}
