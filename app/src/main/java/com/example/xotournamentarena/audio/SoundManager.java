package com.example.xotournamentarena.audio;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.example.xotournamentarena.R;

// SoundPool is ideal for short UI effects. It is faster than MediaPlayer for repeated taps.
public class SoundManager {
    private static SoundManager instance;
    private SoundPool pool;
    private int click, move, win, draw, tournament, achievement;
    private boolean enabled = true;
    private long lastSoundTime = 0;

    public static SoundManager get(Context context) {
        if (instance == null) instance = new SoundManager(context.getApplicationContext());
        return instance;
    }

    private SoundManager(Context context) {
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        pool = new SoundPool.Builder().setMaxStreams(3).setAudioAttributes(attrs).build();
        click = pool.load(context, R.raw.click, 1);
        move = pool.load(context, R.raw.move, 1);
        win = pool.load(context, R.raw.win, 1);
        draw = pool.load(context, R.raw.draw, 1);
        tournament = pool.load(context, R.raw.tournament, 1);
        achievement = pool.load(context, R.raw.achievement, 1);
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        enabled = prefs.getBoolean("sound", true);
    }

    public void setEnabled(Context context, boolean value) {
        enabled = value;
        context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putBoolean("sound", value).apply();
    }

    public boolean isEnabled() { return enabled; }

    private void play(int id) {
        if (!enabled) return;
        long now = System.currentTimeMillis();
        if (now - lastSoundTime < 55) return; // avoids harsh overlapping during fast play
        lastSoundTime = now;
        pool.play(id, 0.35f, 0.35f, 1, 0, 1f);
    }

    public void playClick() { play(click); }
    public void playMove() { play(move); }
    public void playWin() { play(win); }
    public void playDraw() { play(draw); }
    public void playTournamentWin() { play(tournament); }
    public void playAchievement() { play(achievement); }
}
