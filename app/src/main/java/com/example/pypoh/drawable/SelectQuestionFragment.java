package com.example.pypoh.drawable;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pypoh.drawable.Adapter.OptionsAdapter;
import com.example.pypoh.drawable.Matchmaking.MatchingActivity;
import com.example.pypoh.drawable.Model.OptionModel;
import com.example.pypoh.drawable.Model.QuestionModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SelectQuestionFragment extends Fragment {

    private RecyclerView recyclerViewOptions;
    private OptionsAdapter optionsAdapter;

    private QuestionModel questionModel;
    private static OptionsAdapter mAdapter;

    private ArrayList<String> questionsData = new ArrayList<>();
    ChipGroup chipGroup;

    private ArrayList<OptionModel> playerOneQuestion = new ArrayList<>();
    private ArrayList<OptionModel> playerTwoQuestion = new ArrayList<>();

    private ArrayList<String> selectedQuestions = new ArrayList<>();

    int playerCode;

    Button ready;

    // Firebase
    private FirebaseFirestore db;

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
        chipGroup = view.findViewById(R.id.chip_group);
        recyclerViewOptions = view.findViewById(R.id.recycler_options);
        ready = view.findViewById(R.id.button_ready);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = FirebaseFirestore.getInstance();

//        mAdapter = new OptionsAdapter(getContext(),questionsData);
        Bundle bundle = getArguments();

        recyclerViewOptions.setLayoutManager(new GridLayoutManager(getContext(), 3));

        //setup dummy for debug
//        questionsData.add("Dummy1");
//        questionsData.add("Dummy2");
//        questionsData.add("Dummy3");
//        questionsData.add("Dummy4");
//        questionsData.add("Dummy5");
//        questionsData.add("Dummy6");
//        questionsData.add("Dummy7");
//        questionsData.add("Dummy8");
//        questionsData.add("Dummy9");

        if (bundle != null) {
            questionsData = bundle.getStringArrayList("QUESTIONS_KEY");
            playerCode = bundle.getInt("PLAYER_KEY");
            switch (playerCode) {
                case 0:
                    for (int i = 0; i < 9; i++) {
                        assert questionsData != null;
                        playerOneQuestion.add(new OptionModel(questionsData.get(i)));
                    }
                    optionsAdapter = new OptionsAdapter(getContext(), playerOneQuestion);
                    break;
                case 1:
                    for (int i = 9; i < 18; i++) {
                        playerTwoQuestion.add(new OptionModel(questionsData.get(i)));
                    }
                    optionsAdapter = new OptionsAdapter(getContext(), playerTwoQuestion);
                    break;
            }
        }
//        // debug
//        for (String data : questionsData) {
//            playerOneQuestion.add(new OptionModel(data));
//        }
//        optionsAdapter = new OptionsAdapter(getContext(), playerOneQuestion);

        optionsAdapter.setOnItemClickListener(new OptionsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(OptionModel optionModel) {
                int totalSelected = optionsAdapter.getTotalSelected();
                if (totalSelected < 3) {
                    if (optionModel.isSelected()) {
                        optionModel.setSelected(false);
                        optionsAdapter.setTotalSelected(totalSelected - 1);
                        removeChips(optionModel.getQuestion());
                    } else {
                        optionModel.setSelected(true);
                        optionsAdapter.setTotalSelected(totalSelected + 1);
                        addChips(optionModel.getQuestion());
                    }
                } else {
                    if (optionModel.isSelected()) {
                        optionModel.setSelected(false);
                        optionsAdapter.setTotalSelected(totalSelected - 1);
                        removeChips(optionModel.getQuestion());
                    }
                }

                optionsAdapter.notifyDataSetChanged();
            }
        });

        recyclerViewOptions.setAdapter(optionsAdapter);

        final int key = 1;
        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertQuestion();
                intentToBattle(key);
            }
        });

    }

    private void intentToBattle(int key) {
        ((MatchingActivity) getActivity()).setFragment(new CoundownFragment(), key);
    }

    private void insertQuestion() {
        db.collection("room").document().update("question", questionModel.getQuestionList()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("halo_key", "huhu");
                }
            }
        });
    }

    public void addChips(final String question) {
        final Chip chip = new Chip(getContext());
        int paddingDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,
                getContext().getResources().getDisplayMetrics()
        );
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        chip.setText(question);
//        chip.setEnabled(false);
//        chip.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
//        chip.setTextColor(getContext().getResources().getColor(R.color.white));
        selectedQuestions.add(question);
        chipGroup.addView(chip);
    }

    private void removeChips(final String question) {
        int chipsCount = chipGroup.getChildCount();
        if (chipsCount != 0) {
            int i = 0;
            while (i < chipsCount) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                Log.d("chipDebugs", chipsCount + " : " + chip.getText().toString());
                if (chip.getText().toString().equalsIgnoreCase(question)) {
                    selectedQuestions.remove(question);
                    chipGroup.removeView(chip);
                    break;
                }
                i++;
            }
        }
    }

    private void setTag(final List<String> tagList) {
        for (int i = 0; i < tagList.size(); i++) {
            final String tagQuestion = tagList.get(i);
            final Chip chip = new Chip(getContext());
            int paddingDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics()
            );
            chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
            chip.setText(tagQuestion);
            chip.setCloseIconResource(R.drawable.ic_close_black);
            chip.setCloseIconEnabled(true);

            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tagList.remove(tagQuestion);
                    chipGroup.removeView(chip);
                }
            });
            chipGroup.addView(chip);
        }
    }


}





