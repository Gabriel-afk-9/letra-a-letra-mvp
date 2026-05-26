package com.letraaletra.api.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonTypeName("SWAP_POSITION")
public record SwapPositionWsRequest(
        @NotBlank
        String tokenGameId,

        @NotNull
        @Min(0)
        @Max(6)
        Integer position
) implements WsRequestDTO {
}
