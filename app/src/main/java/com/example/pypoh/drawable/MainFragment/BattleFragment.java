package com.example.pypoh.drawable.MainFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pypoh.drawable.Adapter.ModeAdapter;
import com.example.pypoh.drawable.Model.ModeModel;
import com.example.pypoh.drawable.R;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator2;

public class BattleFragment extends Fragment {

    private RecyclerView recyclerViewMode;
    private ModeAdapter modeAdapter;

    private List<ModeModel> modeData = new ArrayList<>();

    // Circle Indicator Utils
    PagerSnapHelper pagerSnapHelper;
    CircleIndicator2 indicator;

    public BattleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_battle, container, false);

        if (modeData.isEmpty()) {
            addModeData();
        }

        // Setup Views
        recyclerViewMode = view.findViewById(R.id.recycler_mode);

        pagerSnapHelper = new PagerSnapHelper();

        indicator = view.findViewById(R.id.modes_indicator);


        return view;
    }

    private void addModeData() {
        modeData.add(new ModeModel(0, "1 VS 1", "Mode ini memungkinkan pemain melawan satu sama lain"));
        modeData.add(new ModeModel(1, "2 VS 2", "Mode ini memungkinkan pemain bertarung dua lawan dua"));
        modeData.add(new ModeModel(2, "Group", "Mode ini memungkinkan pemain bermain bersama maksimal 4 orang"));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Setup RecyclerView
        recyclerViewMode.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        modeAdapter = new ModeAdapter(getContext(), modeData);
        recyclerViewMode.setAdapter(modeAdapter);

        pagerSnapHelper.attachToRecyclerView(recyclerViewMode);
        indicator.attachToRecyclerView(recyclerViewMode, pagerSnapHelper);


    }
}
