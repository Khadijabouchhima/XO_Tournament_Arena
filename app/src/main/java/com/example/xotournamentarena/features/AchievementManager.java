package com.example.xotournamentarena.features;

import java.util.ArrayList;

// Unlocks badges using simple beginner-friendly conditions.
public class AchievementManager {
    private final ArrayList<String> unlocked = new ArrayList<>();

    private char lastWinner = ' ';
    private int winStreak = 0;

    private char lastLoser = ' ';
    private int lossStreak = 0;

    public void checkRound(char winner, int movesUsed, String opponent, String difficulty, String playerSymbol) {
        char player = playerSymbol.charAt(0);
        char aiSymbol = player == 'X' ? 'O' : 'X';
        boolean vsAI = opponent.contains("AI");

        // Draw achievements
        if (winner == ' ') {
            winStreak = 0;
            lossStreak = 0;

            if (vsAI) {
                unlock("Held the Machine");
            }

            if (vsAI && "Hard".equals(difficulty)) {
                unlock("Defensive Genius");
            }

            return;
        }

        // Winner achievements
        unlock("First Win");

        if (winner == lastWinner) {
            winStreak++;
        } else {
            winStreak = 1;
        }

        lastWinner = winner;

        if (winStreak >= 3) {
            unlock("3 Wins in a row");
        }

        if (movesUsed <= 5) {
            unlock("Speed Winner");
        }

        if (vsAI) {
            unlock("AI Challenger");
        }

        if (vsAI && winner == player) {
            unlock("Human Victory");
        }

        if (vsAI && "Hard".equals(difficulty) && winner == player) {
            unlock("Beat Hard AI");
        }

        // Losing achievements against AI
        if (vsAI && winner == aiSymbol) {
            unlock("AI Lesson Learned");

            if ("Hard".equals(difficulty)) {
                unlock("Hard Mode Survivor");
            }
        }

        // Losing streak achievement
        char loser = winner == 'X' ? 'O' : 'X';

        if (loser == lastLoser) {
            lossStreak++;
        } else {
            lossStreak = 1;
        }

        lastLoser = loser;

        if (lossStreak >= 3 && loser == player) {
            unlock("Never Give Up");
        }
    }

    public void checkFinal(int x, int o, int draws, int total, String playerSymbol, String opponent, String difficulty) {
        char player = playerSymbol.charAt(0);
        boolean vsAI = opponent.contains("AI");

        int playerScore = player == 'X' ? x : o;
        int opponentScore = player == 'X' ? o : x;

        if (x == total || o == total) {
            unlock("Clean Sweep");
        }

        if (draws >= total / 2) {
            unlock("Defensive Wall");
        }

        if (draws >= Math.max(3, total / 3)) {
            unlock("Draw Master");
        }

        if (Math.abs(x - o) <= 1 && x != o) {
            unlock("Close Battle");
        }

        if (playerScore < opponentScore && opponentScore - playerScore == 1) {
            unlock("So Close");
        }

        if (vsAI && playerScore == 0 && draws > 0) {
            unlock("Still Standing");
        }

        if (vsAI && "Hard".equals(difficulty) && draws >= 2) {
            unlock("Hard AI Defender");
        }

        if (vsAI && playerScore < opponentScore) {
            unlock("Training Arc Begins");
        }
    }

    private void unlock(String name) {
        if (!unlocked.contains(name)) {
            unlocked.add(name);
        }
    }

    public ArrayList<String> all() {
        return unlocked;
    }

    public String latest() {
        return unlocked.isEmpty() ? null : unlocked.get(unlocked.size() - 1);
    }
}