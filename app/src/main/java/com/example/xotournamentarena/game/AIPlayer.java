package com.example.xotournamentarena.game;

import java.util.ArrayList;
import java.util.Random;

// Three difficulty levels: random, tactical, and minimax.
public class AIPlayer {
    private final Random random = new Random();

    public int chooseMove(char[] board, char aiSymbol, String difficulty) {
        if ("Hard".equals(difficulty)) return minimaxMove(board, aiSymbol);
        if ("Medium".equals(difficulty)) return mediumMove(board, aiSymbol);
        return randomMove(board);
    }

    // Easy: no strategy, just choose any empty cell.
    private int randomMove(char[] board) {
        ArrayList<Integer> moves = emptyCells(board);
        return moves.isEmpty() ? -1 : moves.get(random.nextInt(moves.size()));
    }

    // Medium: first win if possible, then block, otherwise random.
    private int mediumMove(char[] board, char ai) {
        char human = ai == 'X' ? 'O' : 'X';
        int win = findImmediate(board, ai);
        if (win != -1) return win;
        int block = findImmediate(board, human);
        if (block != -1) return block;
        return randomMove(board);
    }

    private int findImmediate(char[] board, char symbol) {
        for (int move : emptyCells(board)) {
            board[move] = symbol;
            boolean wins = winner(board) == symbol;
            board[move] = ' ';
            if (wins) return move;
        }
        return -1;
    }

    // Hard: minimax explores every future board state and picks the best guaranteed outcome.
    private int minimaxMove(char[] board, char ai) {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;
        for (int move : emptyCells(board)) {
            board[move] = ai;
            int score = minimax(board, false, ai, ai == 'X' ? 'O' : 'X');
            board[move] = ' ';
            if (score > bestScore) { bestScore = score; bestMove = move; }
        }
        return bestMove;
    }

    private int minimax(char[] board, boolean maximizing, char ai, char human) {
        char win = winner(board);
        if (win == ai) return 10;
        if (win == human) return -10;
        if (emptyCells(board).isEmpty()) return 0;

        int best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        char symbol = maximizing ? ai : human;
        for (int move : emptyCells(board)) {
            board[move] = symbol;
            int score = minimax(board, !maximizing, ai, human);
            board[move] = ' ';
            best = maximizing ? Math.max(best, score) : Math.min(best, score);
        }
        return best;
    }

    private ArrayList<Integer> emptyCells(char[] board) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < board.length; i++) if (board[i] == ' ') list.add(i);
        return list;
    }

    private char winner(char[] board) {
        for (int[] line : GameEngine.winningLines()) {
            if (board[line[0]] != ' ' && board[line[0]] == board[line[1]] && board[line[1]] == board[line[2]]) return board[line[0]];
        }
        return ' ';
    }
}
