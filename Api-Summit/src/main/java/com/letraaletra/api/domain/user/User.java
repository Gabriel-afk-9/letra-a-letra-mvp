package com.letraaletra.api.domain.user;

import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.user.exceptions.UserAlreadyInGameException;
import com.letraaletra.api.domain.user.stats.UserStats;

public class User {
    private final String id;
    private String nickname;
    private String avatar;
    private final String email;
    private final String hashPassword;
    private final String googleId;
    private String currentGameId;
    private boolean canChangeNickname;
    private final UserStats stats;

    public User(
            String id,
            String nickname,
            String avatar,
            String email,
            String hashPassword,
            String googleId,
            boolean canChangeNickname,
            UserStats stats
    ) {
        this.id = id;
        this.nickname = nickname;
        this.avatar = avatar;
        this.email = email;
        this.hashPassword = hashPassword;
        this.googleId = googleId;
        this.canChangeNickname = canChangeNickname;
        this.stats = stats;
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatarId) {
        this.avatar = avatarId;
    }

    public String getEmail() {
        return email;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public String getGoogleId() {
        return googleId;
    }

    public boolean canChangeNickname() {
        return canChangeNickname;
    }

    public UserStats getStats() {
        return stats;
    }

    public boolean isNotInGame() {
        return currentGameId == null;
    }

    public String getCurrentGameId() {
        return currentGameId;
    }

    public void enterGame(String gameId) {
        if (gameId == null || gameId.isBlank()) {
            throw new GameNotFoundException();
        }

        if (this.currentGameId != null) {
            throw new UserAlreadyInGameException();
        }

        this.currentGameId = gameId;
    }

    public void leaveGame() {
        this.currentGameId = null;
    }

    public void registerMatchResult(boolean isWinner) {
        if (isWinner) {
            stats.registerWin();
        } else {
            stats.registerLose();
        }
    }

    public void setCanChangeNickname(boolean canChangeNickname) {
        this.canChangeNickname = canChangeNickname;
    }
}
