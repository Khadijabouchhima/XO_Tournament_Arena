package com.example.xotournamentarena.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

// Serializable model saved to internal storage as the last tournament result.
public class TournamentResult implements Serializable {
    public int scoreX, scoreO, draws, totalRounds;
    public String playerXName, playerOName;
    public String winner, mode, opponentType, aiDifficulty, dateTime, feedback;
    public ArrayList<String> achievements;

    public TournamentResult(int scoreX, int scoreO, int draws, int totalRounds, String winner,
                            String mode, String opponentType, String aiDifficulty,
                            String feedback, ArrayList<String> achievements,
                            String playerXName, String playerOName) {
        this.scoreX = scoreX;
        this.scoreO = scoreO;
        this.draws = draws;
        this.totalRounds = totalRounds;
        this.winner = winner;
        this.mode = mode;
        this.opponentType = opponentType;
        this.aiDifficulty = aiDifficulty;
        this.feedback = feedback;
        this.achievements = achievements;
        this.playerXName = playerXName;
        this.playerOName = playerOName;
        this.dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
    }

    public double percentX() { return totalRounds == 0 ? 0 : (scoreX * 100.0 / totalRounds); }
    public double percentO() { return totalRounds == 0 ? 0 : (scoreO * 100.0 / totalRounds); }
}
