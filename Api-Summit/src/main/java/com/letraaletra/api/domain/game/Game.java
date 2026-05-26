package com.letraaletra.api.domain.game;

import com.letraaletra.api.domain.game.board.Board;
import com.letraaletra.api.domain.game.exception.GameIsRunningException;
import com.letraaletra.api.domain.game.exception.RoomFullException;
import com.letraaletra.api.domain.game.exception.UserBannedException;
import com.letraaletra.api.domain.game.participant.exception.InvalidRoomPositionException;
import com.letraaletra.api.domain.game.participant.exception.ParticipantAlreadyBannedException;
import com.letraaletra.api.domain.game.participant.exception.ParticipantNotBannedException;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.ParticipantRole;
import com.letraaletra.api.domain.game.exception.UserNotInGameException;
import com.letraaletra.api.domain.game.service.GameStateGenerator;
import com.letraaletra.api.domain.game.state.GameState;
import com.letraaletra.api.domain.user.exceptions.UserAlreadyInGameException;

import java.util.*;

public class Game {
    private final String id;
    private final String code;
    private final String roomName;
    private final Map<String, Participant> participants = new HashMap<>();
    private final Map<Integer, String> positions = new HashMap<>();
    private final RoomSettings roomSettings;
    private final Set<String> blacklist = new HashSet<>();
    private final GameType gameType;
    private final String createdById;
    private String hostId;
    private GameStatus gameStatus;
    private GameState gameState;

    public Game(String id, String code, String roomName, RoomSettings roomSettings, Participant host, GameType gameType) {
        this.id = id;
        this.code = code;
        this.roomName = roomName;
        host.changeRole(ParticipantRole.PLAYER);
        this.participants.put(host.getUserId(), host);
        this.gameType = gameType;
        this.createdById = host.getUserId();
        this.hostId = host.getUserId();
        this.positions.put(0, host.getUserId());
        this.gameStatus = GameStatus.WAITING;
        this.roomSettings = roomSettings;

    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getRoomName() {
        return roomName;
    }

    public GameType getGameType() {
        return gameType;
    }

    public List<Participant> getParticipants() {
        return List.copyOf(participants.values());
    }

    public String getCreatedById() {
        return createdById;
    }

    public String getHostId() {
        return hostId;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public GameState getGameState() {
        return gameState;
    }

    public RoomSettings getRoomSettings() {
        return roomSettings;
    }

    public Participant getParticipantByUserId(String userId) {
        return participants.get(userId);
    }

    public Map<Integer, String> getPositions() {
        return Map.copyOf(positions);
    }

    public void start(Board board, GameStateGenerator stateGenerator) {
        GameState state = stateGenerator.generate(participants.values().stream().toList(), board);

        this.gameStatus = GameStatus.RUNNING;
        this.gameState = state;
    }

    public void join(Participant participant) {
        String userId = participant.getUserId();

        if (isBlackListed(userId)) {
            throw new UserBannedException();
        }

        if (participants.containsKey(userId)) {
            throw new UserAlreadyInGameException();
        }

        int size = participants.size();

        boolean isFullWithoutSpectators = !roomSettings.roomAllowSpectators() && size >= 2;
        boolean isFullWithSpectators = size >= 7;

        if (isFullWithoutSpectators || isFullWithSpectators) {
            throw new RoomFullException();
        }

        ParticipantRole role = determineRole();
        participant.changeRole(role);

        if (role == ParticipantRole.PLAYER) {
            assertMaxPlayers();
        }

        participants.put(userId, participant);
        positions.put(nextAvailablePosition(), userId);
    }

    public void remove(String userId) {
        Participant participant = participants.get(userId);

        if (participant == null) {
            throw new UserNotInGameException();
        }

        participants.remove(userId);
        positions.entrySet().removeIf(entry -> entry.getValue().equals(userId));

        if (gameStatus.equals(GameStatus.RUNNING)) {
            gameState.removePlayer(userId);
        }

        if (participants.isEmpty()) {
            return;
        }

        if (participant.getUserId().equals(hostId)) {
            hostId = participants.keySet().iterator().next();
        }
    }

    public void reconnect(String userId, String sessionId) {
        Participant participant = participants.get(userId);

        if (participant == null) {
            throw new UserNotInGameException();
        }

        participant.connect(sessionId);
    }

    public void changePosition(String userId, int position) {
        if (gameStatus.equals(GameStatus.RUNNING)) {
            throw new GameIsRunningException();
        }

        if (position < 0 || position > 6) {
            throw new InvalidRoomPositionException();
        }

        Participant participant = participants.get(userId);

        if (participant == null) {
            throw new UserNotInGameException();
        }

        if (positions.containsKey(position)) {
            throw new InvalidRoomPositionException();
        }

        ParticipantRole newRole = position >= 2
                ? ParticipantRole.SPECTATOR
                : ParticipantRole.PLAYER;

        if (newRole == ParticipantRole.PLAYER) {
            assertMaxPlayersExcluding(userId);
        }

        participant.changeRole(newRole);

        positions.entrySet().removeIf(entry -> entry.getValue().equals(userId));
        positions.put(position, userId);
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void updateGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Participant findBySession(String sessionId) {
        return participants.values().stream()
                .filter(p -> p.getSocketId().equals(sessionId))
                .findFirst()
                .orElse(null);
    }

    public boolean isBlackListed(String userId) {
        return blacklist.contains(userId);
    }

    public void addToBlackList(String userId) {
        if (blacklist.contains(userId)) {
            throw new ParticipantAlreadyBannedException();
        }

        blacklist.add(userId);
    }

    public void removeFromBlackList(String userId) {
        if (!blacklist.contains(userId)) {
            throw new ParticipantNotBannedException();
        }

        blacklist.remove(userId);
    }

    public int getAmountPlayers() {
        return (int) participants.values().stream()
                .filter(p -> p.getRole() == ParticipantRole.PLAYER)
                .count();
    }

    private ParticipantRole determineRole() {
        long players = participants.values().stream()
                .filter(p -> p.getRole() == ParticipantRole.PLAYER)
                .count();

        return players >= 2
                ? ParticipantRole.SPECTATOR
                : ParticipantRole.PLAYER;
    }

    private void assertMaxPlayers() {
        if (getAmountPlayers() >= 2) {
            throw new RoomFullException();
        }
    }

    private void assertMaxPlayersExcluding(String userId) {
        long players = participants.values().stream()
                .filter(p -> p.getRole() == ParticipantRole.PLAYER)
                .filter(p -> !p.getUserId().equals(userId))
                .count();

        if (players >= 2) {
            throw new RoomFullException();
        }
    }

    private int nextAvailablePosition() {
        for (int i = 0; i < 7; i++) {
            if (!positions.containsKey(i)) {
                return i;
            }
        }
        throw new IllegalStateException("no_available_positions");
    }
}
