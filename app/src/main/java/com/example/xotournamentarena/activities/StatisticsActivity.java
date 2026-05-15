package com.example.xotournamentarena.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xotournamentarena.audio.MusicManager;
import com.example.xotournamentarena.R;
import com.example.xotournamentarena.data.SaveManager;
import com.example.xotournamentarena.audio.SoundManager;
import com.example.xotournamentarena.data.TournamentResult;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    TextView statsBackBtn, emptyStatsText;
    LinearLayout statsContent;

    TextView totalTournamentsText, totalRoundsText;
    TextView xWinsText, oWinsText, drawsText;
    TextView bestSideText, mostPlayedModeText;
    TextView aiMatchesText, hardAiText, statsSummaryText;

    SoundManager sound;

    @Override
    protected void onCreate(Bundle b) {
        MainActivity.applyThemeChoiceStatic(this);

        super.onCreate(b);
        setContentView(R.layout.activity_statistics);

        sound = SoundManager.get(this);

        bindViews();
        setupListeners();
        loadStatistics();
    }

    private void bindViews() {
        statsBackBtn = findViewById(R.id.statsBackBtn);
        emptyStatsText = findViewById(R.id.emptyStatsText);
        statsContent = findViewById(R.id.statsContent);

        totalTournamentsText = findViewById(R.id.totalTournamentsText);
        totalRoundsText = findViewById(R.id.totalRoundsText);

        xWinsText = findViewById(R.id.xWinsText);
        oWinsText = findViewById(R.id.oWinsText);
        drawsText = findViewById(R.id.drawsText);

        bestSideText = findViewById(R.id.bestSideText);
        mostPlayedModeText = findViewById(R.id.mostPlayedModeText);

        aiMatchesText = findViewById(R.id.aiMatchesText);
        hardAiText = findViewById(R.id.hardAiText);

        statsSummaryText = findViewById(R.id.statsSummaryText);
    }

    private void setupListeners() {
        statsBackBtn.setOnClickListener(v -> {
            sound.playClick();
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.startMenuMusic(this);
        loadStatistics();
    }

    private void loadStatistics() {
        ArrayList<TournamentResult> history = SaveManager.loadHistory(this);

        if (history.isEmpty()) {
            emptyStatsText.setVisibility(View.VISIBLE);
            statsContent.setVisibility(View.GONE);
            statsSummaryText.setVisibility(View.GONE);
            return;
        }

        emptyStatsText.setVisibility(View.GONE);
        statsContent.setVisibility(View.VISIBLE);
        statsSummaryText.setVisibility(View.VISIBLE);

        int totalTournaments = history.size();
        int totalRounds = 0;

        int totalXWins = 0;
        int totalOWins = 0;
        int totalDraws = 0;

        int classicCount = 0;
        int fastCount = 0;
        int afkCount = 0;

        int aiMatches = 0;
        int hardAiMatches = 0;

        int pvpMatches = 0;

        for (TournamentResult r : history) {
            totalRounds += r.totalRounds;
            totalXWins += r.scoreX;
            totalOWins += r.scoreO;
            totalDraws += r.draws;

            if ("Classic".equals(r.mode)) {
                classicCount++;
            } else if ("Fast".equals(r.mode)) {
                fastCount++;
            } else if ("AFK".equals(r.mode)) {
                afkCount++;
            }

            if (r.opponentType != null && r.opponentType.contains("AI")) {
                aiMatches++;

                if ("Hard".equals(r.aiDifficulty)) {
                    hardAiMatches++;
                }
            } else {
                pvpMatches++;
            }
        }

        totalTournamentsText.setText("Total tournaments: " + totalTournaments);
        totalRoundsText.setText("Total rounds played: " + totalRounds);

        xWinsText.setText("X round wins: " + totalXWins);
        oWinsText.setText("O round wins: " + totalOWins);
        drawsText.setText("Draw rounds: " + totalDraws);

        bestSideText.setText("Best side: " + bestSide(totalXWins, totalOWins));
        mostPlayedModeText.setText("Most played mode: " + mostPlayedMode(classicCount, fastCount, afkCount));

        aiMatchesText.setText("AI tournaments: " + aiMatches);
        hardAiText.setText("Hard AI tournaments: " + hardAiMatches);

        statsSummaryText.setText(generateSummary(
                totalTournaments,
                totalRounds,
                totalXWins,
                totalOWins,
                totalDraws,
                aiMatches,
                hardAiMatches,
                pvpMatches
        ));
    }

    private String bestSide(int x, int o) {
        if (x > o) {
            return "X";
        }

        if (o > x) {
            return "O";
        }

        return "Balanced";
    }

    private String mostPlayedMode(int classic, int fast, int afk) {
        if (classic >= fast && classic >= afk) {
            return "Classic";
        }

        if (fast >= classic && fast >= afk) {
            return "Fast";
        }

        return "AFK";
    }

    private String generateSummary(
            int tournaments,
            int rounds,
            int xWins,
            int oWins,
            int draws,
            int aiMatches,
            int hardAiMatches,
            int pvpMatches
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append("You played ")
                .append(tournaments)
                .append(tournaments == 1 ? " tournament" : " tournaments")
                .append(" with ")
                .append(rounds)
                .append(" total rounds.\n\n");

        if (xWins > oWins) {
            sb.append("X is currently the strongest side overall.");
        } else if (oWins > xWins) {
            sb.append("O is currently the strongest side overall.");
        } else {
            sb.append("Your results are balanced between X and O.");
        }

        sb.append("\n\n");

        if (draws > Math.max(xWins, oWins)) {
            sb.append("Many rounds ended in draws, which shows defensive gameplay.");
        } else if (aiMatches > pvpMatches) {
            sb.append("You played more tournaments against AI than human players.");
        } else if (hardAiMatches > 0) {
            sb.append("You challenged Hard AI, which adds strong difficulty to your performance.");
        } else {
            sb.append("Your history shows steady tournament activity.");
        }

        return sb.toString();
    }
}