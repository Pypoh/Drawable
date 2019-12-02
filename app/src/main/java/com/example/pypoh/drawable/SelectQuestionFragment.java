package com.example.pypoh.drawable;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pypoh.drawable.Adapter.OptionsAdapter;
import com.example.pypoh.drawable.Matchmaking.MatchingActivity;
import com.example.pypoh.drawable.Model.OptionModel;
import com.example.pypoh.drawable.Model.QuestionModel;
import com.example.pypoh.drawable.Model.TempModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class SelectQuestionFragment extends Fragment {

    private RecyclerView recyclerViewOptions;
    private OptionsAdapter optionsAdapter;

    private MatchingFragment matchingFragment = new MatchingFragment();
    private static OptionsAdapter mAdapter;

    QuestionModel questionModel = new QuestionModel();

    private ArrayList<String> questionsData = new ArrayList<>();
    private ArrayList<String> selectedQuestions = new ArrayList<>();
    ChipGroup chipGroup;
    TempModel tempModel = new TempModel();

    private ArrayList<OptionModel> playerOneQuestion = new ArrayList<>();
    private ArrayList<OptionModel> playerTwoQuestion = new ArrayList<>();

    private int playerCode;

    private String roomId;

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

        roomId = bundle.getString("ROOM_KEY");
        playerCode = bundle.getInt("PLAYER_KEY");
        Log.d("roomId", roomId);
        Log.d("playerCode", String.valueOf(playerCode));


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


        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertQuestion();
                intentToBattle();
            }
        });

    }

    private void intentToBattle() {
        Bundle bundle = new Bundle();
        bundle.putString("ROOM_KEY", roomId);
        bundle.putInt("PLAYER_KEY", playerCode);
        questionModel = null;
        ((MatchingActivity) getActivity()).setFragmentWithBundle(new CoundownFragment(), bundle);
    }

    private void insertQuestion() {
        /*WriteBatch batch = db.batch();
        DocumentReference col = db.collection("room").document(roomId).collection("question").document();*/
        for (int i = 0; i < selectedQuestions.size(); i++) {
            Log.d("arraySize", String.valueOf(selectedQuestions.size()));
            tempModel.setQuestion(questionModel.getQuestionList().get(i));
            db.collection("room").document(roomId).collection("question").document().set(tempModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
//                        Toast.makeText(getContext(), "Question successful inserted", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            /*batch.set(col, tempModel);
            batch.set(col, questionModel);*/

        }
        /*batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                }
            }
        });*/
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
        questionModel = new QuestionModel(selectedQuestions);
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





