package com.letraaletra.api.presentation.controller;

import com.letraaletra.api.application.command.game.FindByCodeCommand;
import com.letraaletra.api.application.command.game.FindByTokenCommand;
import com.letraaletra.api.application.output.game.FindByCodeOutput;
import com.letraaletra.api.application.output.game.GetGamesOutput;
import com.letraaletra.api.application.usecase.game.FindByCodeUseCase;
import com.letraaletra.api.application.usecase.game.FindByTokenGameIdUseCase;
import com.letraaletra.api.application.usecase.game.GetPublicGamesUseCase;
import com.letraaletra.api.application.output.game.FindByTokenOutput;
import com.letraaletra.api.presentation.dto.response.http.SuccessResponseDTO;
import com.letraaletra.api.presentation.dto.response.http.FindByCodeResponseDTO;
import com.letraaletra.api.presentation.dto.response.http.FindByTokenResponseDTO;
import com.letraaletra.api.domain.game.GameMessages;
import com.letraaletra.api.presentation.dto.response.game.GetGamesResponseDTO;
import com.letraaletra.api.presentation.mappers.game.FindByCodeMapper;
import com.letraaletra.api.presentation.mappers.game.FindByTokenMapper;
import com.letraaletra.api.presentation.mappers.game.GetGamesMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/game")
public class GameController {

    @Autowired
    private GetPublicGamesUseCase getPublicGamesUseCase;

    @Autowired
    private GetGamesMapper getGamesMapper;

    @Autowired
    private FindByTokenMapper findByTokenMapper;

    @Autowired
    private FindByCodeMapper findByCodeMapper;

    @Autowired
    private FindByTokenGameIdUseCase findByTokenGameIdUseCase;

    @Autowired
    private FindByCodeUseCase findByCode;

    @GetMapping
    public ResponseEntity<SuccessResponseDTO<GetGamesResponseDTO>> getGames() {
        GetGamesOutput output = getPublicGamesUseCase.execute();

        GetGamesResponseDTO dto = getGamesMapper.toResponseDTO(output);

        return ApiResponse.success(dto, GameMessages.GAMES_FOUND.getMessage());
    }

    @GetMapping("/{tokenGameId}")
    public ResponseEntity<SuccessResponseDTO<FindByTokenResponseDTO>> getGameById(@Valid @PathVariable String tokenGameId) {
        FindByTokenCommand command = findByTokenMapper.toCommand(tokenGameId);

        FindByTokenOutput output = findByTokenGameIdUseCase.execute(command);

        FindByTokenResponseDTO dto = findByTokenMapper.toResponseDTO(output);

        return ApiResponse.success(dto, GameMessages.GAME_FOUND.getMessage());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<SuccessResponseDTO<FindByCodeResponseDTO>> getGameByCode(@Valid @PathVariable String code) {
        FindByCodeCommand command = findByCodeMapper.toCommand(code);

        FindByCodeOutput output = findByCode.execute(command);

        FindByCodeResponseDTO dto = findByCodeMapper.toResponseDTO(output);

        return ApiResponse.success(dto);
    }
}
