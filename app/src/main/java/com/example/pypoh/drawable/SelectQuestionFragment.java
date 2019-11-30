package com.example.pypoh.drawable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pypoh.drawable.Adapter.OptionsAdapter;
import com.example.pypoh.drawable.Model.QuestionModel;

import java.util.ArrayList;
import java.util.List;

public class SelectQuestionFragment extends Fragment {

    private RecyclerView recyclerViewOptions;
    private OptionsAdapter optionsAdapter;

    private QuestionModel questionModel;

    private ArrayList<String> questionsData;

    private ArrayList<String> playerOneQuestion = new ArrayList<>();
    private ArrayList<String> playerTwoQuestion = new ArrayList<>();

    int playerCode;

    public SelectQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_question, container, false);

        recyclerViewOptions = view.findViewById(R.id.recycler_options);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            questionsData = bundle.getStringArrayList("QUESTIONS_KEY");
            playerCode = bundle.getInt("PLAYER_KEY");
            for (int i = 0; i < 9; i++) {
                assert questionsData != null;
                playerOneQuestion.add(questionsData.get(i));
            }
            for (int i = 10; i < 18; i++) {
                playerTwoQuestion.add(questionsData.get(i));
            }

        }

        recyclerViewOptions.setLayoutManager(new GridLayoutManager(getContext(), 3));
        if (playerCode == 0) {
            optionsAdapter = new OptionsAdapter(getContext(), playerOneQuestion);
        } else {
            optionsAdapter = new OptionsAdapter(getContext(), playerTwoQuestion);
        }

        recyclerViewOptions.setAdapter(optionsAdapter);

    }


}
