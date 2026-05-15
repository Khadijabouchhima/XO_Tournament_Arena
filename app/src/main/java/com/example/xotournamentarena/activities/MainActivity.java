package com.example.xotournamentarena.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.Context;

import com.example.xotournamentarena.audio.MusicManager;
import com.example.xotournamentarena.R;
import com.example.xotournamentarena.audio.SoundManager;

public class MainActivity extends AppCompatActivity {
    Spinner roundSpinner, modeSpinner, opponentSpinner, aiSpinner;
    TextView aiLabel, playerNamesLabel;
    RadioGroup symbolGroup;
    EditText playerXNameInput, playerONameInput;
    SoundManager sound;
    boolean isLoadingSettings = false;
    private void updatePlayerNameFields() {
        String opponentType = opponentSpinner.getSelectedItem().toString();
        boolean isAI = opponentType.contains("AI");

        if (isAI) {
            playerNamesLabel.setText("Player Name");
            playerXNameInput.setHint("Your name");
            playerONameInput.setText("");
            playerONameInput.setVisibility(View.GONE);

            aiSpinner.setEnabled(true);
            aiSpinner.setVisibility(View.VISIBLE);
            aiLabel.setVisibility(View.VISIBLE);
        } else {
            playerNamesLabel.setText("Player Names");
            playerXNameInput.setHint("Player X name");
            playerONameInput.setVisibility(View.VISIBLE);
            playerONameInput.setHint("Player O name");

            aiSpinner.setEnabled(false);
            aiSpinner.setVisibility(View.GONE);
            aiLabel.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        applyThemeChoice();
        setContentView(R.layout.activity_main);

        sound = SoundManager.get(this);

        bindViews();
        setupSpinners();
        setupSavedSettings();

        opponentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                updatePlayerNameFields();
            }

            public void onNothingSelected(AdapterView<?> p) {}
        });


        findViewById(R.id.playBtn).setOnClickListener(v -> startGame());

        findViewById(R.id.rulesBtn).setOnClickListener(v -> {
            sound.playClick();
            startActivity(new Intent(this, RulesActivity.class));
        });
        findViewById(R.id.settingsBtn).setOnClickListener(v -> {
            sound.playClick();
                startActivity(new Intent(this, SettingsActivity.class));
        });
        findViewById(R.id.statisticsBtn).setOnClickListener(v -> {
            sound.playClick();
            startActivity(new Intent(this, StatisticsActivity.class));
        });
        findViewById(R.id.loadBtn).setOnClickListener(v -> {
            sound.playClick();
            startActivity(new Intent(this, HistoryActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.startMenuMusic(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void bindViews() {
        roundSpinner = findViewById(R.id.roundSpinner);
        modeSpinner = findViewById(R.id.modeSpinner);
        opponentSpinner = findViewById(R.id.opponentSpinner);
        aiSpinner = findViewById(R.id.aiSpinner);

        aiLabel = findViewById(R.id.aiLabel);
        playerNamesLabel = findViewById(R.id.playerNamesLabel);
        symbolGroup = findViewById(R.id.symbolGroup);

        playerXNameInput = findViewById(R.id.playerXNameInput);
        playerONameInput = findViewById(R.id.playerONameInput);
    }

    private void setupSpinners() {
        ArrayAdapter<String> roundsAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"1", "3", "5", "10", "15"}
        );
        roundsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roundSpinner.setAdapter(roundsAdapter);

        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Classic", "Fast", "AFK"}
        );
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);

        ArrayAdapter<String> opponentAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Player vs Player", "Player vs AI"}
        );
        opponentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opponentSpinner.setAdapter(opponentAdapter);

        ArrayAdapter<String> aiAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Easy", "Medium", "Hard"}
        );
        aiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aiSpinner.setAdapter(aiAdapter);
    }

    private void setupSavedSettings() {
        isLoadingSettings = true;

        updatePlayerNameFields();

        isLoadingSettings = false;
    }
    private void applyThemeChoice() {
        applyThemeChoiceStatic(this);
    }

    public static void applyThemeChoiceStatic(Context context) {
        boolean dark = context.getSharedPreferences("settings", MODE_PRIVATE)
                .getBoolean("dark", true);

        int newMode = dark
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO;

        if (AppCompatDelegate.getDefaultNightMode() != newMode) {
            AppCompatDelegate.setDefaultNightMode(newMode);
        }
    }

    private void startGame() {
        sound.playClick();

        String opponentType = opponentSpinner.getSelectedItem().toString();
        String chosenSymbol = symbolGroup.getCheckedRadioButtonId() == R.id.symbolX ? "X" : "O";

        String playerName = playerXNameInput.getText().toString().trim();
        String playerXName = playerXNameInput.getText().toString().trim();
        String playerOName = playerONameInput.getText().toString().trim();

        if (opponentType.contains("AI")) {
            if (playerName.isEmpty()) {
                playerName = "Player";
            }

            if ("X".equals(chosenSymbol)) {
                playerXName = playerName;
                playerOName = "AI";
            } else {
                playerXName = "AI";
                playerOName = playerName;
            }
        } else {
            if (playerXName.isEmpty()) {
                playerXName = "Player X";
            }

            if (playerOName.isEmpty()) {
                playerOName = "Player O";
            }
        }

        Intent i = new Intent(this, GameActivity.class);

        i.putExtra("rounds", Integer.parseInt(roundSpinner.getSelectedItem().toString()));
        i.putExtra("mode", modeSpinner.getSelectedItem().toString());
        i.putExtra("opponent", opponentType);
        i.putExtra("difficulty", aiSpinner.getSelectedItem().toString());
        i.putExtra("symbol", chosenSymbol);

        i.putExtra("playerXName", playerXName);
        i.putExtra("playerOName", playerOName);

        MusicManager.pauseMenuMusic();

        startActivity(i);
    }
}