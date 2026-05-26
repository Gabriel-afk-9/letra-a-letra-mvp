package com.letraaletra.api.presentation.dto.request.player;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
public sealed interface PlayerActionDTO
    permits
        RevealActionDTO,
        BlockActionDTO,
        UnblockActionDTO,
        TrapActionDTO,
        DetectTrapsActionDTO,
        SpyActionDTO,
        FreezeActionDTO,
        UnfreezeActionDTO,
        BlindActionDTO,
        LanternActionDTO,
        ImmunityActionDTO
{}
