package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.FindByCodeCommand;
import com.letraaletra.api.application.output.game.FindByCodeOutput;
import com.letraaletra.api.application.port.GameQueryService;
import com.letraaletra.api.application.usecase.UseCase;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;

public class FindByCodeUseCase implements UseCase<FindByCodeCommand, FindByCodeOutput> {
    private final GameQueryService gameQueryService;
    private final TokenService tokenService;

    public FindByCodeUseCase(GameQueryService gameQueryService, TokenService tokenService) {
        this.gameQueryService = gameQueryService;
        this.tokenService = tokenService;
    }

    public FindByCodeOutput execute(FindByCodeCommand command) {
        Game game = gameQueryService.findByCode(command.code());

        validateGame(game);

        String token = tokenService.generateToken(game.getId());

        return buildReturn(token);
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }
    }

    private FindByCodeOutput buildReturn(String token) {
        return new FindByCodeOutput(
                token
        );
    }
}
