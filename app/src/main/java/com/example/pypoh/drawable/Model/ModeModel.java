package com.example.pypoh.drawable.Model;

public class ModeModel {

    private int mode; // 0 = 1vs1, 1 = 2vs2, 2 = group
    private String name;
    private String description;

    public ModeModel(int mode, String name, String description) {
        this.mode = mode;
        this.name = name;
        this.description = description;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
