package com.example.xotournamentarena.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.xotournamentarena.R;

public class SplashActivity extends AppCompatActivity {

    private int getSplashDelay() {
        return getSharedPreferences("settings", MODE_PRIVATE)
                .getInt("splash_duration", 2000);
    }
    @Override
    protected void onCreate(Bundle b) {
        MainActivity.applyThemeChoiceStatic(this);

        super.onCreate(b);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, getSplashDelay());
    }
}