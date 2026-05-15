package com.example.xotournamentarena.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.xotournamentarena.audio.MusicManager;
import com.example.xotournamentarena.R;
import com.example.xotournamentarena.audio.SoundManager;

public class RulesActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_rules);
        findViewById(R.id.backBtn).setOnClickListener(v -> { SoundManager.get(this).playClick(); finish(); });
    }
    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.startMenuMusic(this);
    }
}
