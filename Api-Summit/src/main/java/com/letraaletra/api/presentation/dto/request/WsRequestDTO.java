package com.letraaletra.api.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)

public sealed interface WsRequestDTO
    permits
        CreateGameWsRequest,
        JoinGameWsRequest,
        JoinMatchmakingGameWsRequest,
        SwapPositionWsRequest,
        StartGameWsRequest,
        LeftGameWsRequest,
        KickParticipantWsRequest,
        BanParticipantWsRequest,
        UnbanParticipantWsRequest,
        PlayerActionWsRequest,
        DiscardPowerWsRequest
{}
