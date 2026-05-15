package com.example.xotournamentarena.activities;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.*;

import com.example.xotournamentarena.game.AIPlayer;
import com.example.xotournamentarena.features.AchievementManager;
import com.example.xotournamentarena.features.FeedbackGenerator;
import com.example.xotournamentarena.game.GameEngine;
import com.example.xotournamentarena.audio.MusicManager;
import com.example.xotournamentarena.R;
import com.example.xotournamentarena.audio.SoundManager;
import com.example.xotournamentarena.game.TournamentManager;
import com.example.xotournamentarena.data.TournamentResult;

public class GameActivity extends AppCompatActivity {
    TextView roundText, statusText, leaderText, modeText;
    TextView xScoreNum, oScoreNum, drawScoreNum;
    LinearLayout roundOverlay, leaveOverlay;
    TextView roundOverlayTitle, roundOverlaySubtitle;
    Button cancelLeaveBtn, confirmLeaveBtn;
    ProgressBar progress;
    Button[] cells = new Button[9];
    GameEngine engine = new GameEngine();
    TournamentManager tournament;
    AchievementManager achievements = new AchievementManager();
    AIPlayer ai = new AIPlayer();
    SoundManager sound;
    Handler handler = new Handler();
    String mode, opponent, difficulty, chosenSymbol;
    String playerXName, playerOName;
    char humanSymbol, aiSymbol;
    int movesThisRound = 0, fastWins = 0;
    boolean locked = false;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        MusicManager.pauseMenuMusic();
        setContentView(R.layout.activity_game);
        sound = SoundManager.get(this);
        int total = getIntent().getIntExtra("rounds", 5);
        mode = getIntent().getStringExtra("mode"); opponent = getIntent().getStringExtra("opponent");
        difficulty = getIntent().getStringExtra("difficulty");
        chosenSymbol = getIntent().getStringExtra("symbol");

        if (chosenSymbol == null || chosenSymbol.trim().isEmpty()) {
            chosenSymbol = "X";
        }

        humanSymbol = chosenSymbol.charAt(0);
        aiSymbol = humanSymbol == 'X' ? 'O' : 'X';
        playerXName = getIntent().getStringExtra("playerXName");
        playerOName = getIntent().getStringExtra("playerOName");

        if (playerXName == null || playerXName.trim().isEmpty()) {
            playerXName = "Player X";
        }

        if (playerOName == null || playerOName.trim().isEmpty()) {
            playerOName = "Player O";
        }
        tournament = new TournamentManager(total);
        bindViews(); setupCells(); setupLeaveOverlay(); updateHeader();
        if ("AFK".equals(mode)) {
            handler.postDelayed(this::autoMove, 700);
        } else if (opponent.contains("AI") && engine.currentPlayer == aiSymbol) {
            locked = true;
            statusText.setText("AI is thinking...");
            handler.postDelayed(this::aiMove, 500);
        }
    }
    private String roundWinnerName(char winner) {
        if (winner == 'X') {
            return playerXName;
        }

        if (winner == 'O') {
            return playerOName;
        }

        return "Draw";
    }
    private void showRoundTransition(char winner) {
        String subtitle;

        if (winner == ' ') {
            subtitle = "Previous round ended in a draw";
        } else {
            subtitle = roundWinnerName(winner) + " won the previous round";
        }

        if (tournament.hasMoreRounds()) {
            roundOverlayTitle.setText("Round " + (tournament.currentRound + 1));
            roundOverlaySubtitle.setText(subtitle);
        } else {
            roundOverlayTitle.setText("Tournament Complete");
            roundOverlaySubtitle.setText("Preparing final results...");
        }

        // Transition sound effect
        sound.playDraw();

        locked = true;

        roundOverlay.clearAnimation();
        roundOverlay.setAlpha(0f);
        roundOverlay.setVisibility(android.view.View.VISIBLE);

        roundOverlay.animate()
                .alpha(1f)
                .setDuration(300)
                .withEndAction(() -> {

                    if (tournament.hasMoreRounds()) {
                        prepareNextRoundBehindOverlay();
                    }

                    handler.postDelayed(() -> {
                        roundOverlay.animate()
                                .alpha(0f)
                                .setDuration(300)
                                .withEndAction(() -> {
                                    roundOverlay.setVisibility(android.view.View.GONE);

                                    if (tournament.hasMoreRounds()) {
                                        locked = false;
                                        updateHeader();

                                        if ("AFK".equals(mode)) {
                                            handler.postDelayed(this::autoMove, 550);
                                        } else {
                                            startAiTurnIfNeeded();
                                        }
                                    } else {
                                        finishTournament();
                                    }
                                })
                                .start();
                    }, 1600);
                })
                .start();
    }
    private void bindViews() {
        roundText = findViewById(R.id.roundText);
        statusText = findViewById(R.id.statusText);
        leaderText = findViewById(R.id.leaderText);
        modeText = findViewById(R.id.modeText);
        progress = findViewById(R.id.progress);

        xScoreNum = findViewById(R.id.xScoreNum);
        oScoreNum = findViewById(R.id.oScoreNum);
        drawScoreNum = findViewById(R.id.drawScoreNum);

        roundOverlay = findViewById(R.id.roundOverlay);
        roundOverlayTitle = findViewById(R.id.roundOverlayTitle);
        roundOverlaySubtitle = findViewById(R.id.roundOverlaySubtitle);

        leaveOverlay = findViewById(R.id.leaveOverlay);
        cancelLeaveBtn = findViewById(R.id.cancelLeaveBtn);
        confirmLeaveBtn = findViewById(R.id.confirmLeaveBtn);
        int[] ids = {
                R.id.cell0, R.id.cell1, R.id.cell2,
                R.id.cell3, R.id.cell4, R.id.cell5,
                R.id.cell6, R.id.cell7, R.id.cell8
        };

        for (int i = 0; i < 9; i++) {
            cells[i] = findViewById(ids[i]);
        }
    }
    private void setupLeaveOverlay() {
        cancelLeaveBtn.setOnClickListener(v -> {
            sound.playClick();
            hideLeaveOverlay();
        });

        confirmLeaveBtn.setOnClickListener(v -> {
            sound.playClick();
            MusicManager.startMenuMusic(this);

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

            finish();
        });
    }
    private void showLeaveOverlay() {
        locked = true;

        leaveOverlay.clearAnimation();
        leaveOverlay.setAlpha(0f);
        leaveOverlay.setVisibility(android.view.View.VISIBLE);

        leaveOverlay.animate()
                .alpha(1f)
                .setDuration(200)
                .start();
    }

    private void hideLeaveOverlay() {
        leaveOverlay.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> {
                    leaveOverlay.setVisibility(android.view.View.GONE);
                    locked = false;

                    if (!"AFK".equals(mode) && opponent.contains("AI") && engine.currentPlayer == aiSymbol) {
                        locked = true;
                        statusText.setText("AI is thinking...");
                        handler.postDelayed(this::aiMove, 500);
                    }
                })
                .start();
    }
    private void setupCells() {
        for (int i = 0; i < 9; i++) {
            final int index = i;
            cells[i].setOnClickListener(v -> {
                if (locked || "AFK".equals(mode)) return;
                humanMove(index);
            });
        }
    }

    private void humanMove(int index) {
        if (opponent.contains("AI") && engine.currentPlayer != humanSymbol) {
            return;
        }

        if (!engine.makeMove(index)) {
            return;
        }

        afterMove(index);

        if (!locked && opponent.contains("AI") && engine.currentPlayer == aiSymbol) {
            locked = true;
            statusText.setText("AI is thinking...");
            handler.postDelayed(this::aiMove, 420);
        }
    }

    private void aiMove() {
        int move = ai.chooseMove(engine.board, aiSymbol, difficulty);

        if (move != -1) {
            engine.makeMove(move);
            afterMove(move);
        }

        if (engine.winner() == ' ' && !engine.isDraw()) {
            locked = false;
        }
    }

    private void autoMove() {
        if (!"AFK".equals(mode) || isFinishing()) return;
        String level = engine.currentPlayer == 'X' ? "Medium" : "Easy";
        int move = ai.chooseMove(engine.board, engine.currentPlayer, level);
        if (move != -1) { engine.makeMove(move); afterMove(move); }
        if (!locked) handler.postDelayed(this::autoMove, 650);
    }

    private void afterMove(int index) {
        movesThisRound++;
        cells[index].setText(String.valueOf(engine.board[index]));
        cells[index].startAnimation(AnimationUtils.loadAnimation(this, R.anim.pop));
        sound.playMove();
        char winner = engine.winner();
        if (winner != ' ' || engine.isDraw()) finishRound(winner);
        else updateHeader();
    }
    private String finalWinnerName() {
        if (tournament.scoreX > tournament.scoreO) {
            return playerXName;
        }

        if (tournament.scoreO > tournament.scoreX) {
            return playerOName;
        }

        return "Draw Tournament";
    }
    private void finishRound(char winner) {
        locked = true;
        tournament.recordRound(winner);
        if (winner != ' ') {
            if (movesThisRound <= 5) fastWins++;
            for (int i : engine.winningCells()) cells[i].setBackgroundResource(R.drawable.cell_win_bg);
            sound.playWin();
            String winnerName = winner == 'X' ? playerXName : playerOName;
            Toast.makeText(this, "Round winner: " + winnerName, Toast.LENGTH_SHORT).show();
        } else {
            sound.playDraw();
            Toast.makeText(this, "Draw round", Toast.LENGTH_SHORT).show();
        }
        achievements.checkRound(winner, movesThisRound, opponent, difficulty, chosenSymbol);
        if (achievements.latest() != null) sound.playAchievement();
        updateHeader();
        int delay = "Fast".equals(mode) ? 400 : 700;
        handler.postDelayed(() -> showRoundTransition(winner), delay);
    }
    private void prepareNextRoundBehindOverlay() {
        tournament.nextRound();
        engine.reset();
        movesThisRound = 0;

        for (Button b : cells) {
            b.setText("");
            b.setBackgroundResource(R.drawable.cell_bg);
        }

        updateHeader();
    }
    private void startAiTurnIfNeeded() {
        if (!"AFK".equals(mode) && opponent.contains("AI") && engine.currentPlayer == aiSymbol) {
            locked = true;
            statusText.setText("AI is thinking...");
            handler.postDelayed(this::aiMove, 500);
        }
    }

    private void finishTournament() {
        achievements.checkFinal(
                tournament.scoreX,
                tournament.scoreO,
                tournament.draws,
                tournament.totalRounds,
                chosenSymbol,
                opponent,
                difficulty
        );
        String feedback = FeedbackGenerator.generate(
                tournament.scoreX,
                tournament.scoreO,
                tournament.draws,
                tournament.totalRounds,
                fastWins,
                chosenSymbol,
                opponent,
                difficulty,
                playerXName,
                playerOName
        );
        TournamentResult result = new TournamentResult(
                tournament.scoreX,
                tournament.scoreO,
                tournament.draws,
                tournament.totalRounds,
                finalWinnerName(),
                mode,
                opponent,
                difficulty,
                feedback,
                achievements.all(),
                playerXName,
                playerOName
        );

        progress.setProgress(100);

        sound.playTournamentWin();
        ResultActivity.open(this, result, false);
        finish();
    }

    private void updateHeader() {
        roundText.setText("Round " + tournament.currentRound + " / " + tournament.totalRounds);

        String currentName = engine.currentPlayer == 'X' ? playerXName : playerOName;

        statusText.setText(
                ("AFK".equals(mode) ? "AFK Mode Active • " : "") +
                        currentName + "'s turn"
        );

        if (engine.currentPlayer == 'X') {
            statusText.setTextColor(getColor(R.color.player_x));
        } else {
            statusText.setTextColor(getColor(R.color.player_o));
        }

        xScoreNum.setText(String.valueOf(tournament.scoreX));
        oScoreNum.setText(String.valueOf(tournament.scoreO));
        drawScoreNum.setText(String.valueOf(tournament.draws));

        leaderText.setText("Leader: " + displayLeader());

        modeText.setText(
                mode + " · " + opponent +
                        (opponent.contains("AI") ? " · " + difficulty : "")
        );

        progress.setProgress(
                (int) ((tournament.currentRound - 1) * 100.0 / tournament.totalRounds)
        );
    }
    private String displayLeader() {
        if (tournament.scoreX > tournament.scoreO) {
            return playerXName;
        }

        if (tournament.scoreO > tournament.scoreX) {
            return playerOName;
        }

        return "Balanced";
    }
    @Override
    public void onBackPressed() {
        if (leaveOverlay.getVisibility() == android.view.View.VISIBLE) {
            hideLeaveOverlay();
        } else {
            showLeaveOverlay();
        }
    }
}
