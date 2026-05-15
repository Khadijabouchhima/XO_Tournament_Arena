package com.example.xotournamentarena.game;

// Keeps tournament-level scores separate from board-level rules.
public class TournamentManager {
    public int scoreX = 0, scoreO = 0, draws = 0, currentRound = 1;
    public final int totalRounds;

    public TournamentManager(int totalRounds) { this.totalRounds = totalRounds; }

    public void recordRound(char winner) {
        if (winner == 'X') scoreX++;
        else if (winner == 'O') scoreO++;
        else draws++;
    }

    public boolean hasMoreRounds() { return currentRound < totalRounds; }
    public void nextRound() { currentRound++; }

    public String leader() {
        if (scoreX > scoreO) return "Player X";
        if (scoreO > scoreX) return "Player O";
        return "Balanced";
    }

    public String finalWinner() {
        if (scoreX > scoreO) return "Player X";
        if (scoreO > scoreX) return "Player O";
        return "Draw Tournament";
    }
}
