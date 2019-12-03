package com.example.pypoh.drawable.Score;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pypoh.drawable.Adapter.ScoreAdapter;
import com.example.pypoh.drawable.MainActivity;
import com.example.pypoh.drawable.Model.RoundModel;
import com.example.pypoh.drawable.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScoreFragment extends Fragment {
    private TextView name_host,name_opponent,score_host, score_opponent;

    private String nama_host, nama_opponent;
    private List<RoundModel> roundModels;
    private RecyclerView recyclerView;
    private ScoreAdapter scoreAdapter;
    private Button buttonExit;

    public ScoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_score, container, false);
        recyclerView = view.findViewById(R.id.recyler_score);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        name_host = view.findViewById(R.id.name_host);
        name_opponent = view.findViewById(R.id.name_opponent);
        score_host = view.findViewById(R.id.score_host);
        score_opponent = view.findViewById(R.id.score_opponent);
        buttonExit = view.findViewById(R.id.btn_exit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMain();
            }
        });

        Gson gson = new Gson();
        Bundle bundle = getArguments();
        String json = bundle.getString("ROUND_LIST");
        Type type = new TypeToken<ArrayList<RoundModel>>() {}.getType();
        nama_host = bundle.getString("NAMA_HOST");
        nama_opponent = bundle.getString("NAMA_OPPONENT");
        roundModels = gson.fromJson(json, type);
        // Inflate the layout for this fragment

        // Set Views Data
        name_host.setText(nama_host);
        name_opponent.setText(nama_opponent);

        scoreAdapter = new ScoreAdapter(getContext(), roundModels);
        recyclerView.setAdapter(scoreAdapter);

        score_host.setText(String.valueOf(scoreAdapter.getTotalHost()));
        score_opponent.setText(String.valueOf(scoreAdapter.getTotalOpponent()));

        return view;
    }

    private void toMain() {
        Intent toMainIntent = new Intent(getContext(), MainActivity.class);
        startActivity(toMainIntent);
        getActivity().finish();
    }
}
