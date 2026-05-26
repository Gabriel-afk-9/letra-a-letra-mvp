package com.letraaletra.api.domain.user.stats;

public class UserStats {
    private int totalMatchs;
    private int totalWins;
    private int winStreak;
    private int points;

    public UserStats(int totalMatchs, int totalWins, int winStreak, int points) {
        this.totalMatchs = totalMatchs;
        this.totalWins = totalWins;
        this.winStreak = winStreak;
        this.points = points;
    }

    public int getTotalMatchs() {
        return totalMatchs;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public int getPoints() {
        return points;
    }

    public void registerWin() {
        totalMatchs++;
        totalWins++;
        winStreak++;
    }

    public void registerLose() {
        totalMatchs++;
        winStreak = 0;
    }

    private void incrementPoints(int userPoints, int opponentPoints) {
        points = Math.max(userPoints, opponentPoints) - Math.min(userPoints, opponentPoints);
    }
}
