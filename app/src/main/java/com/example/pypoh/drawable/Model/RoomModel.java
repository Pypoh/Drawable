package com.example.pypoh.drawable.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RoomModel {
    @SerializedName("battleTag_host")
    private String battleTag_opponent;
    @SerializedName("battleTag_opponent")
    private String battleTag_host;
    @SerializedName("score_host")
    private int score_host;
    @SerializedName("score_opponent")
    private int score_opponent;

    private List<String> question = new ArrayList<>();
    private List<String> availableQuestion = new ArrayList<>();

    public RoomModel(String battleTag_host, String battleTag_opponent, int score_host, int score_opponent) {
        this.battleTag_host = battleTag_host;
        this.battleTag_opponent = battleTag_opponent;
        this.score_host = score_host;
        this.score_opponent = score_opponent;
    }

    public RoomModel() {

    }

    public List<String> getQuestion() {
        return question;
    }

    public void setQuestion(List<String> question) {
        this.question = question;
    }

    public List<String> getAvailableQuestion() {
        return availableQuestion;
    }

    public void setAvailableQuestion(List<String> availableQuestion) {
        this.availableQuestion = availableQuestion;
    }

    public String getBattleTag_host() {
        return battleTag_host;
    }

    public void setBattleTag_host(String battleTag_host) {
        this.battleTag_host = battleTag_host;
    }

    public String getBattleTag_opponent() {
        return battleTag_opponent;
    }

    public void setBattleTag_opponent(String battleTag_opponent) {
        this.battleTag_opponent = battleTag_opponent;
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
