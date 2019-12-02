package com.example.pypoh.drawable.Drawing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.pypoh.drawable.Model.RoundModel;
import com.example.pypoh.drawable.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DrawingActivity extends AppCompatActivity {

    private List<String> questionList = new ArrayList<>();

    private String roomId;
    private int playerCode;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        db = FirebaseFirestore.getInstance();

        // get data from past activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // get primitive data
            roomId = bundle.getString("ROOM_KEY");
            playerCode = bundle.getInt("PLAYER_KEY");

            // get gson data
            Gson objectJson = new Gson();
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            questionList = objectJson.fromJson(bundle.getString("QUESTION_LIST"), listType);
            setupStage();
        } else {
            // do something
        }
    }

    private void setupStage() {
        if (questionList != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("questionlistSize", questionList.size() + " ");
                        for (int i = 0; i < questionList.size(); i++) {
                            Bundle bundle = new Bundle();
                            bundle.putString("ROOM_KEY", roomId);
                            bundle.putInt("PLAYER_KEY", playerCode);
                            bundle.putString("QUESTION", questionList.get(i));
                            bundle.putInt("ITERASI", i);
                            final DrawingFragment drawingFragment = new DrawingFragment();
                            setFragmentWithBundle(drawingFragment, bundle);
                            Thread.sleep(10000);
                            final int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    uploadScore(drawingFragment, questionList.get(finalI));
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.d("exeptionQuestionChange", e.toString());
//                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    private void uploadScore(DrawingFragment drawingFragment, String question) {
        switch (playerCode) {
            case 0:
                db.collection("room").document(roomId).collection("question").document(question).update("score_host", drawingFragment.getResult()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                break;
            case 1:
                db.collection("room").document(roomId).collection("question").document(question).update("score_opponent", drawingFragment.getResult()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                break;
        }
    }


    public void setFragmentWithBundle(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame_drawing, fragment, "MAIN_FRAGMENT");
        ft.addToBackStack(null);
        ft.commit();
    }
}
