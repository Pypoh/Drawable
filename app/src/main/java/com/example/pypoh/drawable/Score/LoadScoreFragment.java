package com.example.pypoh.drawable.Score;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pypoh.drawable.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoadScoreFragment extends Fragment {

    private String roomId;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public LoadScoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_load_score, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        // load data from firebase
        Bundle dataForResult = getArguments();
        if (dataForResult != null) {
            roomId = dataForResult.getString("ROOM_KEY");
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    public void loadScore(){


    }
}
