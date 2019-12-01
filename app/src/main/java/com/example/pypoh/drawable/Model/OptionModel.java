package com.example.pypoh.drawable.Model;

public class OptionModel {
    private String question;
    private boolean selected;

    public OptionModel(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
