package com.example.pypoh.drawable.Model;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class QuestionModel {
    private ArrayList<String> questionList = new ArrayList<>();

    public QuestionModel(ArrayList<String> questionList) {
        this.questionList = questionList;
    }

    public QuestionModel() {
    }

    public ArrayList<String> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(ArrayList<String> questionList) {
        this.questionList = questionList;
    }
}
