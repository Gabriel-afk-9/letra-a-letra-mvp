package com.letraaletra.api.domain.game.board.service;

import com.letraaletra.api.domain.game.state.GameMode;
import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.board.cell.PowerRarity;
import com.letraaletra.api.domain.game.board.cell.PowerType;
import com.letraaletra.api.domain.game.board.position.Position;

import java.util.*;
import java.util.stream.Collectors;

public class CellFactory {
    private final Random random;
    private final Map<PowerRarity, List<PowerType>> powersByRarity;

    public CellFactory(Random random) {
        this.random = random;
        this.powersByRarity = Arrays.stream(PowerType.values())
                .collect(Collectors.groupingBy(PowerType::getPowerRarity));
    }

    public Cell create(char letter, Position position, GameMode gameMode) {
        Optional<PowerType> drop = selectDrop(gameMode);

          return new Cell(
                  letter,
                  position,
                  drop.orElse(null)
          );
    }

    private Optional<PowerType> selectDrop(GameMode gameMode) {
        double chancePerCell = gameMode.getChancePerCell();

        double hasDrop = random.nextDouble();

        if (hasDrop >= chancePerCell) return Optional.empty();

        Map<PowerRarity, Double> percentages = gameMode.getPercentages();

        double roll = random.nextDouble();

        double cumulative = 0;

        cumulative += percentages.get(PowerRarity.LEGENDARY);
        if (roll <= cumulative) return Optional.of(getRandomPower(PowerRarity.LEGENDARY));

        cumulative += percentages.get(PowerRarity.EPIC);
        if (roll <= cumulative) return Optional.of(getRandomPower(PowerRarity.EPIC));

        cumulative += percentages.get(PowerRarity.RARE);
        if (roll <= cumulative) return Optional.of(getRandomPower(PowerRarity.RARE));

        return Optional.of(getRandomPower(PowerRarity.COMMON));
    }

    private PowerType getRandomPower(PowerRarity rarity) {
        List<PowerType> list = powersByRarity.get(rarity);

        return list.get(random.nextInt(list.size()));
    }
}
