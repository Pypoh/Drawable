package com.example.pypoh.drawable.Score;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pypoh.drawable.Drawing.DrawingActivity;
import com.example.pypoh.drawable.Model.RoomModel;
import com.example.pypoh.drawable.Model.RoundModel;
import com.example.pypoh.drawable.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class LoadScoreFragment extends Fragment {

    private String roomId;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userHost;
    private String userOpponent;
    private RoomModel roomModel;

    private List<RoundModel> listResult = new ArrayList<>();

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
        loadRoom();
        return view;
    }

    public void loadScore() {
        listResult.clear();
        db.collection("room").document(roomId).collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        RoundModel roundModel = documentSnapshot.toObject(RoundModel.class);
                        listResult.add(roundModel);
                    }
                    Gson gson = new Gson();
                    String json = gson.toJson(listResult);
                    Bundle bundle = new Bundle();
                    bundle.putString("NAMA_HOST", userHost);
                    bundle.putString("NAMA_OPPONENT", userOpponent);
                    bundle.putString("ROUND_LIST", json);
                    try {
                        ((DrawingActivity) getActivity()).setFragmentWithBundle(new ScoreFragment(), bundle);
                    } catch (Exception e) {

                    }
                }
            }
        });

    }

    private void loadRoom() {
        db.collection("room").document(roomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        roomModel = documentSnapshot.toObject(RoomModel.class);
                        loadHost();
                    }

                }
            }
        });
    }

    private void loadOpponent() {
        if (roomModel != null) {
            db.collection("users").document(roomModel.getBattleTag_opponent()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc != null) {
                            userOpponent = doc.getString("name");
                            loadScore();
                        }
                    }
                }
            });
        }
    }

    private void loadHost() {
        if (roomModel != null) {
            db.collection("users").document(roomModel.getBattleTag_host()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc != null) {
                            userHost = doc.getString("name");
                            loadOpponent();
                        }
                    }
                }
            });
        }
    }
}
