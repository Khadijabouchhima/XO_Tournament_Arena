package com.example.xotournamentarena.audio;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.xotournamentarena.R;

public class MusicManager {

    private static MediaPlayer menuMusic;

    public static void startMenuMusic(Context context) {
        boolean musicEnabled = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getBoolean("menu_music", true);

        if (!musicEnabled) {
            pauseMenuMusic();
            return;
        }

        if (menuMusic == null) {
            menuMusic = MediaPlayer.create(context.getApplicationContext(), R.raw.menu_music);
            menuMusic.setLooping(true);
            menuMusic.setVolume(0.25f, 0.25f);
        }

        if (!menuMusic.isPlaying()) {
            menuMusic.start();
        }
    }

    public static void pauseMenuMusic() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.pause();
        }
    }

    public static void stopMenuMusic() {
        if (menuMusic != null) {
            if (menuMusic.isPlaying()) {
                menuMusic.stop();
            }

            menuMusic.release();
            menuMusic = null;
        }
    }
}
