package com.letraaletra.api.presentation.dto.response.game;

public enum MatchmakingStatus {
    SEARCHING("searching"),
    FOUNDED("founded");

    private final String response;

    MatchmakingStatus(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
