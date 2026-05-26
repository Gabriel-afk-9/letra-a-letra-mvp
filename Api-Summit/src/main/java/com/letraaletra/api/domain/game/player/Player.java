package com.letraaletra.api.domain.game.player;

import com.letraaletra.api.domain.game.board.cell.PowerType;
import com.letraaletra.api.domain.game.player.effect.FreezeEffect;
import com.letraaletra.api.domain.game.player.effect.PlayerEffect;
import com.letraaletra.api.domain.game.player.exception.InvalidPlayerActionException;

import java.util.*;

public class Player {
    private final String userId;
    private final LinkedHashMap<String, PowerType> inventory = new LinkedHashMap<>();
    private int score = 0;
    private final List<PlayerEffect> effects = new ArrayList<>();
    private int passedTurn = 0;

    public Player(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void addToInventory(PowerType powerType) {
        if (inventory.size() == 5) return;

        String id = UUID.randomUUID().toString();

        inventory.put(id, powerType);
    }

    public Map<String, PowerType> getInventory() {
        return Map.copyOf(inventory);
    }

    public List<PlayerEffect> getEffects() {
        return List.copyOf(effects);
    }

    public int getPassedTurn() {
        return passedTurn;
    }

    public void removeFromInventoryOrThrow(String id) {
        if (!inventory.containsKey(id)) {
            throw new InvalidPlayerActionException();
        }

        inventory.remove(id);
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        this.score++;
    }

    public void applyEffect(PlayerEffect effect) {
        effects.add(effect);
    }

    public void decrementEffectDuration() {
        effects.forEach(PlayerEffect::onTurnPassed);
        effects.removeIf(PlayerEffect::canRemove);
    }

    public void removeEffect(Class<? extends PlayerEffect> type) {
        effects.removeIf(type::isInstance);
    }

    public boolean canNotPlay() {
        return effects.stream().anyMatch(effect -> effect instanceof FreezeEffect) &&
                inventory.values().stream().noneMatch(power -> power == PowerType.UNFREEZE || power == PowerType.IMMUNITY);
    }

    public void passedTurn() {
        passedTurn++;
    }

    public void resetPassedTurn() {
        passedTurn = 0;
    }
}
