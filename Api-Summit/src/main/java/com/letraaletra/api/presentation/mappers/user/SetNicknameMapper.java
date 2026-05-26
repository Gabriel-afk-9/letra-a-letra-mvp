package com.letraaletra.api.presentation.mappers.user;

import com.letraaletra.api.application.command.user.SetNicknameCommand;
import com.letraaletra.api.application.output.user.SetNicknameOutput;
import com.letraaletra.api.presentation.dto.request.user.SetNicknameRequestDTO;
import com.letraaletra.api.presentation.dto.response.user.SetNicknameResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class SetNicknameMapper {
    public SetNicknameCommand toCommand(SetNicknameRequestDTO request, String userId) {
        return new SetNicknameCommand(
                userId,
                request.nickname()
        );
    }

    public SetNicknameResponseDTO toResponseDTO(SetNicknameOutput output) {
        return new SetNicknameResponseDTO(output.nickname());
    }
}
