package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "event"
)

public sealed interface WsResponseDTO
    permits
        CreateGameResponseDTO,
        JoinGameResponseDTO,
        JoinMatchmakingResponseDTO,
        LeftGameResponseDTO,
        StartGameResponseDTO,
        GameOverResponseDTO,

        KickParticipantResponseDTO,
        BanParticipantResponseDTO,
        UnbanParticipantResponseDTO,
        ModerationResponseDTO,

        SwapPositionResponseDTO,

        DisconnectParticipantResponseDTO,
        ReconnectParticipantResponseDTO,

        PlayerActionResponseDTO,
        DiscardPowerResponseDTO,

        ErrorResponseDTO
{}
