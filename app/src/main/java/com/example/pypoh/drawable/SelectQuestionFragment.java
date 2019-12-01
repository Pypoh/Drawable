package com.example.pypoh.drawable;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pypoh.drawable.Adapter.OptionsAdapter;
import com.example.pypoh.drawable.Model.OptionModel;
import com.example.pypoh.drawable.Model.QuestionModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class SelectQuestionFragment extends Fragment {

    private RecyclerView recyclerViewOptions;
    private OptionsAdapter optionsAdapter;

    private QuestionModel questionModel;

    private ArrayList<String> questionsData;

    private ArrayList<OptionModel> playerOneQuestion = new ArrayList<>();
    private ArrayList<OptionModel> playerTwoQuestion = new ArrayList<>();
    private ChipGroup chipGroup;

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
        chipGroup = view.findViewById(R.id.tag_group);
//
//        optionsAdapter.setOnItemClickListener(new OptionsAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//
//            }
//        });

        return view;
    }

    private void  setTag(final List<String> tagList){
        for (int i = 0; i < tagList.size(); i++){
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        recyclerViewOptions.setLayoutManager(new GridLayoutManager(getContext(), 3));

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
        recyclerViewOptions.setAdapter(optionsAdapter);

        // set chip
//        setTag(questionsData);


    }


}
