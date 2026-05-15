package com.example.xotournamentarena.data;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

// Handles tournament history using Android internal storage.
public class SaveManager {
    private static final String FILE_NAME = "tournament_history.dat";

    public static void save(Context context, TournamentResult result) throws Exception {
        ArrayList<TournamentResult> history = loadHistory(context);

        // Newest result first
        history.add(0, result);

        FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(history);
        out.close();
    }

    public static ArrayList<TournamentResult> loadHistory(Context context) {
        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            ObjectInputStream in = new ObjectInputStream(fis);

            ArrayList<TournamentResult> history =
                    (ArrayList<TournamentResult>) in.readObject();

            in.close();
            return history;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static TournamentResult loadLatest(Context context) {
        ArrayList<TournamentResult> history = loadHistory(context);

        if (history.isEmpty()) {
            return null;
        }

        return history.get(0);
    }

    // Keep this for old code compatibility if something still calls SaveManager.load(this)
    public static TournamentResult load(Context context) {
        return loadLatest(context);
    }

    public static boolean clear(Context context) {
        return context.deleteFile(FILE_NAME);
    }

    public static boolean hasHistory(Context context) {
        return !loadHistory(context).isEmpty();
    }
}
