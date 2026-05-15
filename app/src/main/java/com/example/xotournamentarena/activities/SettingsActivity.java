package com.example.xotournamentarena.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xotournamentarena.audio.MusicManager;
import com.example.xotournamentarena.R;
import com.example.xotournamentarena.data.SaveManager;
import com.example.xotournamentarena.audio.SoundManager;

public class SettingsActivity extends AppCompatActivity {

    Switch settingsThemeSwitch, settingsSoundSwitch, settingsMusicSwitch;
    RadioGroup splashDurationGroup;
    Button resetSavedResultBtn;
    TextView settingsBackBtn;

    SoundManager sound;
    boolean isLoadingSettings = false;

    @Override
    protected void onCreate(Bundle b) {
        MainActivity.applyThemeChoiceStatic(this);

        super.onCreate(b);
        setContentView(R.layout.activity_settings);

        sound = SoundManager.get(this);

        bindViews();
        loadSettings();
        setupListeners();
    }
    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.startMenuMusic(this);
    }
    private void bindViews() {
        settingsBackBtn = findViewById(R.id.settingsBackBtn);

        settingsThemeSwitch = findViewById(R.id.settingsThemeSwitch);
        settingsSoundSwitch = findViewById(R.id.settingsSoundSwitch);
        settingsMusicSwitch = findViewById(R.id.settingsMusicSwitch);

        splashDurationGroup = findViewById(R.id.splashDurationGroup);
        resetSavedResultBtn = findViewById(R.id.resetSavedResultBtn);
    }

    private void loadSettings() {
        isLoadingSettings = true;

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);

        boolean dark = prefs.getBoolean("dark", true);
        boolean soundEnabled = prefs.getBoolean("sound", true);
        boolean musicEnabled = prefs.getBoolean("menu_music", true);
        int splashDuration = prefs.getInt("splash_duration", 2000);

        settingsThemeSwitch.setChecked(dark);
        settingsSoundSwitch.setChecked(soundEnabled);
        settingsMusicSwitch.setChecked(musicEnabled);

        if (splashDuration == 5000) {
            splashDurationGroup.check(R.id.splash5s);
        } else {
            splashDurationGroup.check(R.id.splash2s);
        }

        isLoadingSettings = false;
    }

    private void setupListeners() {
        settingsBackBtn.setOnClickListener(v -> {
            sound.playClick();
            finish();
        });

        settingsThemeSwitch.setOnCheckedChangeListener((button, checked) -> {
            if (isLoadingSettings) return;

            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .putBoolean("dark", checked)
                    .apply();

            sound.playClick();

            AppCompatDelegate.setDefaultNightMode(
                    checked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });
        settingsSoundSwitch.setOnCheckedChangeListener((button, checked) -> {
            if (isLoadingSettings) return;

            sound.setEnabled(this, checked);

            if (checked) {
                sound.playClick();
            }
        });

        settingsMusicSwitch.setOnCheckedChangeListener((button, checked) -> {
            if (isLoadingSettings) return;

            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .putBoolean("menu_music", checked)
                    .apply();

            if (checked) {
                sound.playClick();
                MusicManager.startMenuMusic(this);
            } else {
                MusicManager.pauseMenuMusic();
            }

            Toast.makeText(
                    this,
                    checked ? "Menu music enabled" : "Menu music disabled",
                    Toast.LENGTH_SHORT
            ).show();
        });

        splashDurationGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (isLoadingSettings) return;

            int duration = checkedId == R.id.splash5s ? 5000 : 2000;

            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .putInt("splash_duration", duration)
                    .apply();

            sound.playClick();

            Toast.makeText(
                    this,
                    "Splash duration set to " + (duration / 1000) + " seconds",
                    Toast.LENGTH_SHORT
            ).show();
        });

        resetSavedResultBtn.setOnClickListener(v -> {
            sound.playClick();

            boolean deleted = SaveManager.clear(this);

            Toast.makeText(
                    this,
                    deleted ? "Match history cleared" : "No match history found",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}