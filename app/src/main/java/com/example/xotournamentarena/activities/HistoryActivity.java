package com.example.xotournamentarena.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.view.ViewGroup;

import com.example.xotournamentarena.audio.MusicManager;
import com.example.xotournamentarena.R;
import com.example.xotournamentarena.data.SaveManager;
import com.example.xotournamentarena.audio.SoundManager;
import com.example.xotournamentarena.data.TournamentResult;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    TextView historyBackBtn, emptyHistoryText;
    ListView historyList;
    Button clearHistoryBtn;

    ArrayList<TournamentResult> history;
    SoundManager sound;

    @Override
    protected void onCreate(Bundle b) {
        MainActivity.applyThemeChoiceStatic(this);

        super.onCreate(b);
        setContentView(R.layout.activity_history);

        sound = SoundManager.get(this);

        bindViews();
        loadHistory();
        setupListeners();
    }

    private void bindViews() {
        historyBackBtn = findViewById(R.id.historyBackBtn);
        emptyHistoryText = findViewById(R.id.emptyHistoryText);
        historyList = findViewById(R.id.historyList);
        clearHistoryBtn = findViewById(R.id.clearHistoryBtn);
    }

    private void loadHistory() {
        history = SaveManager.loadHistory(this);

        if (history.isEmpty()) {
            emptyHistoryText.setVisibility(View.VISIBLE);
            historyList.setVisibility(View.GONE);
            clearHistoryBtn.setVisibility(View.GONE);
        } else {
            emptyHistoryText.setVisibility(View.GONE);
            historyList.setVisibility(View.VISIBLE);
            clearHistoryBtn.setVisibility(View.VISIBLE);

            historyList.setAdapter(new HistoryAdapter());
        }
    }

    private void setupListeners() {
        historyBackBtn.setOnClickListener(v -> {
            sound.playClick();
            finish();
        });

        historyList.setOnItemClickListener((parent, view, position, id) -> {
            sound.playClick();

            TournamentResult selected = history.get(position);
            ResultActivity.open(this, selected, true);
        });

        clearHistoryBtn.setOnClickListener(v -> {
            sound.playClick();

            new AlertDialog.Builder(this)
                    .setTitle("Clear history?")
                    .setMessage("All saved tournament results will be deleted.")
                    .setPositiveButton("Clear", (dialog, which) -> {
                        SaveManager.clear(this);
                        loadHistory();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.startMenuMusic(this);
        loadHistory();
    }

    private class HistoryAdapter extends ArrayAdapter<TournamentResult> {

        HistoryAdapter() {
            super(HistoryActivity.this, R.layout.history_item, history);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = convertView;

            if (item == null) {
                item = getLayoutInflater().inflate(R.layout.history_item, parent, false);
            }

            TournamentResult r = history.get(position);

            TextView winnerText = item.findViewById(R.id.historyWinnerText);
            TextView scoreText = item.findViewById(R.id.historyScoreText);
            TextView detailsText = item.findViewById(R.id.historyDetailsText);

            String playerX = safeName(r.playerXName, "Player X");
            String playerO = safeName(r.playerOName, "Player O");

            winnerText.setText("🏆 " + r.winner);

            scoreText.setText(
                    playerX + " (X): " + r.scoreX +
                            "   •   " +
                            playerO + " (O): " + r.scoreO +
                            "   •   Draws: " + r.draws
            );

            detailsText.setText(
                    r.mode + " • " +
                            r.opponentType +
                            formatDifficulty(r.aiDifficulty) +
                            " • " +
                            r.dateTime
            );

            return item;
        }

        private String safeName(String value, String fallback) {
            if (value == null || value.trim().isEmpty()) {
                return fallback;
            }

            return value;
        }

        private String formatDifficulty(String difficulty) {
            if (difficulty == null || difficulty.trim().isEmpty()) {
                return "";
            }

            return " • " + difficulty;
        }
    }
}