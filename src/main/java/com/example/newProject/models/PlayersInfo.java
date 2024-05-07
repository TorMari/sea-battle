package com.example.newProject.models;

import org.springframework.stereotype.Component;

@Component
public class PlayersInfo {
    private String player1;
    private String player2;
    private int[] gameScore;

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public int[] getGameScore() {
        return gameScore;
    }

    public void setGameScore(int score1, int score2) {
        this.gameScore[0] = score1;
        this.gameScore[1] = score2;
    }

    public PlayersInfo() {
        this.gameScore = new int[]{0, 0};
    }
}
