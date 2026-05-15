package com.example.xotournamentarena.features;

// Simple readable rules create smart-sounding tournament feedback.
public class FeedbackGenerator {

    public static String generate(
            int x,
            int o,
            int draws,
            int total,
            int fastWins,
            String playerSymbol,
            String opponent,
            String difficulty,
            String playerXName,
            String playerOName
    ) {
        boolean vsAI = opponent.contains("AI");

        char player = playerSymbol.charAt(0);

        int playerScore = player == 'X' ? x : o;
        int opponentScore = player == 'X' ? o : x;

        String playerName = player == 'X' ? playerXName : playerOName;
        String opponentName = player == 'X' ? playerOName : playerXName;

        int diff = Math.abs(x - o);

        // AI-specific feedback
        if (vsAI) {
            if (playerScore > opponentScore) {
                if ("Hard".equals(difficulty)) {
                    return playerName + " defeated Hard AI with strong strategy and smart pressure.";
                }

                return playerName + " won against the AI with confident play.";
            }

            if (playerScore < opponentScore) {
                if ("Hard".equals(difficulty)) {
                    if (draws >= 2) {
                        return "Hard AI won, but " + playerName + " defended well and forced several close rounds.";
                    }

                    return "Hard AI won this tournament, but " + playerName + " gained valuable experience.";
                }

                if (diff <= 1) {
                    return "The AI won by a small margin. " + playerName + " stayed close until the end.";
                }

                return "The AI won the tournament, but every loss is useful training for the next match.";
            }

            // AI draw tournament
            if (draws >= total / 2) {
                return playerName + " held the AI to a defensive tournament with many draws.";
            }

            return playerName + " and the AI finished evenly matched.";
        }

        // Player vs Player feedback
        if (draws >= total / 2) {
            return "Defensive game with many draws. Both players were careful and hard to beat.";
        }

        if (diff == 0) {
            return "Balanced match between " + playerXName + " and " + playerOName + ".";
        }

        if (diff >= total * 0.6) {
            return "Dominating performance by " + (x > o ? playerXName : playerOName) + ".";
        }

        if (fastWins >= Math.max(2, total / 3)) {
            return "Fast victories shaped the tournament.";
        }

        if (diff <= 1) {
            return "Close battle. " + (x > o ? playerXName : playerOName) + " won under pressure.";
        }

        return "Strong tournament performance by " + (x > o ? playerXName : playerOName) + ".";
    }
}