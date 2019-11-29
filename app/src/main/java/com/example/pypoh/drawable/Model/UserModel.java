package com.example.pypoh.drawable.Model;

import com.google.gson.annotations.SerializedName;

public class UserModel {

    @SerializedName("battleTag")
    private String battleTag;
    @SerializedName("name")
    private String name;
    @SerializedName("level")
    private int level;
    @SerializedName("matches")
    private int matches;
    @SerializedName("win")
    private int win;
    @SerializedName("loss")
    private int loss;
    @SerializedName("online")
    private boolean online;

    public UserModel(String battleTag, String name, int level, int matches, int win, int loss, boolean online) {
        this.battleTag = battleTag;
        this.name = name;
        this.level = level;
        this.matches = matches;
        this.win = win;
        this.loss = loss;
        this.online = online;
    }
    public UserModel() {

    }

    public String getBattleTag() {
        return battleTag;
    }

    public void setBattleTag(String battleTag) {
        this.battleTag = battleTag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getLoss() {
        return loss;
    }

    public void setLoss(int loss) {
        this.loss = loss;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
