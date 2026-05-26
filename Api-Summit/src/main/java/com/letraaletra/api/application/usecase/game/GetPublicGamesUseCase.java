package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.output.game.GetGamesOutput;
import com.letraaletra.api.application.port.GameQueryService;
import com.letraaletra.api.application.usecase.UseCaseWithoutInput;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.security.TokenService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetPublicGamesUseCase implements UseCaseWithoutInput<GetGamesOutput> {
    private final GameQueryService gameQueryService;
    private final TokenService tokenService;

    public GetPublicGamesUseCase(GameQueryService gameQueryService, TokenService tokenService) {
        this.gameQueryService = gameQueryService;
        this.tokenService = tokenService;
    }

    public GetGamesOutput execute() {
        List<Game> games = gameQueryService.getPublic();

        Map<String, String> tokens = new HashMap<>();

        for (Game game : games) {
            String token = tokenService.generateToken(game.getId());

            tokens.put(game.getId(), token);
        }

        return buildReturn(games, tokens);
    }

    private GetGamesOutput buildReturn(List<Game> games, Map<String, String> tokens) {
        return new GetGamesOutput(
                games,
                tokens
        );
    }
}
