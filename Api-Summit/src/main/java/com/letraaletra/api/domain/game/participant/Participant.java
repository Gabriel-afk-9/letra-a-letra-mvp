package com.letraaletra.api.domain.game.participant;

public class Participant {
    private final String userId;
    private final String nickname;
    private final String avatar;
    private String socketId;
    private ParticipantRole role;
    private boolean connected;

    public Participant(String userId, String socketId, String nickname, String avatar) {
        this.userId = userId;
        this.socketId = socketId;
        this.nickname = nickname;
        this.avatar = avatar;
        this.connected = true;
        this.role = ParticipantRole.SPECTATOR;
    }

    public String getUserId() {
        return userId;
    }

    public String getSocketId() {
        return socketId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public ParticipantRole getRole() {
        return role;
    }

    public void changeRole(ParticipantRole role) {
        this.role = role;
    }

    public boolean isConnected() {
        return connected;
    }

    public void connect(String sessionId) {
        this.connected = true;
        this.socketId = sessionId;
    }

    public void disconnect() {
        this.connected = false;
    }
}
