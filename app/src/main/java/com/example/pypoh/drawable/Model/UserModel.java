package com.example.pypoh.drawable.Model;

import com.google.gson.annotations.SerializedName;

public class UserModel {
    @SerializedName("id")
    private String id;
    @SerializedName("BattleTag")
    private String battleTag;
    @SerializedName("email")
    private String email;
    @SerializedName("level")
    private int level;

    public UserModel(String id, String battleTag, String email, int level) {
        this.id = id;
        this.battleTag = battleTag;
        this.email = email;
        this.level = level;
    }

    public UserModel(String battleTag) {
        this.battleTag = battleTag;
    }

    public UserModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBattleTag() {
        return battleTag;
    }

    public void setName(String name) {
        this.battleTag = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}
