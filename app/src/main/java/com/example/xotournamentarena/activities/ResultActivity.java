package com.example.xotournamentarena.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import com.example.xotournamentarena.audio.MusicManager;
import com.example.xotournamentarena.R;
import com.example.xotournamentarena.data.SaveManager;
import com.example.xotournamentarena.audio.SoundManager;
import com.example.xotournamentarena.data.TournamentResult;

import java.util.Locale;

public class ResultActivity extends AppCompatActivity {
    public static void open(Context context, TournamentResult result, boolean loaded) {
        Intent i = new Intent(context, ResultActivity.class);
        i.putExtra("result", result);
        i.putExtra("loaded", loaded);
        context.startActivity(i);
    }
    private String formatAchievements(TournamentResult r) {
        if (r.achievements.isEmpty()) return "No achievements unlocked yet";
        StringBuilder sb = new StringBuilder();
        for (String a : r.achievements) sb.append("• ").append(a).append("\n");
        return sb.toString().trim();
    }
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_result);

        SoundManager sound = SoundManager.get(this);

        TextView resultText = findViewById(R.id.resultText);
        TextView achievementsText = findViewById(R.id.achievementsText);

        TournamentResult loadedResult =
                (TournamentResult) getIntent().getSerializableExtra("result");

        boolean loadedFromHistory = getIntent().getBooleanExtra("loaded", false);

        if (loadedResult == null) {
            loadedResult = SaveManager.loadLatest(this);
            loadedFromHistory = true;
        }

        if (loadedResult == null) {
            resultText.setText("No saved tournament found");
            achievementsText.setText("");
            return;
        }

        final TournamentResult r = loadedResult;

        if (!loadedFromHistory) {
            try {
                SaveManager.save(this, r);
                sound.playTournamentWin();
            } catch (Exception e) {
                Toast.makeText(this, "Auto-save failed", Toast.LENGTH_SHORT).show();
            }
        }

        String playerX = r.playerXName == null || r.playerXName.trim().isEmpty()
                ? "Player X"
                : r.playerXName;

        String playerO = r.playerOName == null || r.playerOName.trim().isEmpty()
                ? "Player O"
                : r.playerOName;

        resultText.setText("Winner: " + r.winner + "\n" +
                playerX + " (X): " + r.scoreX + "\n" +
                playerO + " (O): " + r.scoreO + "\n" +
                "Draws: " + r.draws + "\n" +
                "Total rounds: " + r.totalRounds + "\n" +
                "Win % " + playerX + ": " + String.format(Locale.getDefault(), "%.1f", r.percentX()) + "%\n" +
                "Win % " + playerO + ": " + String.format(Locale.getDefault(), "%.1f", r.percentO()) + "%\n" +
                "Mode: " + r.mode + "\n" +
                "Opponent: " + r.opponentType + "\n" +
                "AI: " + r.aiDifficulty + "\n" +
                "Date: " + r.dateTime + "\n\n" +
                r.feedback);

        achievementsText.setText(formatAchievements(r));

        findViewById(R.id.saveBtn).setOnClickListener(v -> {
            sound.playClick();
            startActivity(new Intent(this, HistoryActivity.class));
        });

        findViewById(R.id.playAgainBtn).setOnClickListener(v -> {
            sound.playClick();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.homeBtn).setOnClickListener(v -> {
            sound.playClick();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.startMenuMusic(this);
    }
}
