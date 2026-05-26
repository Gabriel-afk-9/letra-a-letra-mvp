package com.letraaletra.api.presentation.mappers.user;

import com.letraaletra.api.application.command.user.SetAvatarCommand;
import com.letraaletra.api.application.output.user.SetAvatarOutput;
import com.letraaletra.api.presentation.dto.request.user.SetAvatarRequestDTO;
import com.letraaletra.api.presentation.dto.response.user.SetAvatarResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class SetAvatarMapper {
    public SetAvatarCommand toCommand(SetAvatarRequestDTO request, String userId) {
        return new SetAvatarCommand(
                userId,
                request.avatar()
        );
    }

    public SetAvatarResponseDTO toResponseDTO(SetAvatarOutput output) {
        return new SetAvatarResponseDTO(
                output.avatar()
        );
    }
}
