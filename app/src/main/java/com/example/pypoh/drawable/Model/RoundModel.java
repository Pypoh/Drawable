package com.example.pypoh.drawable.Model;

public class RoundModel {
    private String question;
    private int score_host, score_opponent;

    public RoundModel(String question) {
        this.question = question;
    }

    public RoundModel() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getScore_host() {
        return score_host;
    }

    public void setScore_host(int score_host) {
        this.score_host = score_host;
    }

    public int getScore_opponent() {
        return score_opponent;
    }

    public void setScore_opponent(int score_opponent) {
        this.score_opponent = score_opponent;
    }
}
