package com.example.xotournamentarena.game;

import java.util.ArrayList;
import java.util.Arrays;

// Handles only board rules. This makes the logic easy to test and explain.
public class GameEngine {
    public char[] board = new char[9];
    public char currentPlayer = 'X';

    public GameEngine() { reset(); }

    public void reset() {
        Arrays.fill(board, ' ');
        currentPlayer = 'X';
    }

    public boolean makeMove(int index) {
        if (index < 0 || index > 8 || board[index] != ' ') return false;
        board[index] = currentPlayer;
        currentPlayer = currentPlayer == 'X' ? 'O' : 'X';
        return true;
    }

    public char winner() {
        int[][] lines = winningLines();
        for (int[] line : lines) {
            if (board[line[0]] != ' ' && board[line[0]] == board[line[1]] && board[line[1]] == board[line[2]]) {
                return board[line[0]];
            }
        }
        return ' ';
    }

    public boolean isDraw() { return winner() == ' ' && availableMoves().isEmpty(); }

    public ArrayList<Integer> availableMoves() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) if (board[i] == ' ') list.add(i);
        return list;
    }

    public int[] winningCells() {
        for (int[] line : winningLines()) {
            if (board[line[0]] != ' ' && board[line[0]] == board[line[1]] && board[line[1]] == board[line[2]]) return line;
        }
        return new int[0];
    }

    public static int[][] winningLines() {
        return new int[][]{{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    }
}
