package com.example.pypoh.drawable;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pypoh.drawable.Model.QuestionModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CoundownFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private QuestionModel questionModel = new QuestionModel();

    private TextView tv_countDown;
//    private int[] i = {3, 2, 1};

    private ArrayList<String> questionList = new ArrayList<>();

    private MatchingFragment matchingFragment = new MatchingFragment();

//    private Runnable setCountDown;

    private int PLAYER_CODE;
    private String ROOM_ID;

//    private Handler getQuestionHandler = new Handler();
    /*private Runnable getQuestionRunnable = new Runnable() {
        @Override
        public void run() {
            if (questionModel == null) {
                getQuestionList();
            }
        }
    };

    Runnable checkData = new Runnable() {
        @Override
        public void run() {
            getQuestionData();
        }
    };*/



    public CoundownFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coundown, container, false);
        tv_countDown = view.findViewById(R.id.tv_counter);

        // Inflate the layout for this fragment
        return view;
    }

    private void deleteNotification() {
        String uid = mAuth.getCurrentUser().getUid();
        Bundle bundle = getArguments();
        int playerCode = bundle.getInt("STATUS_KEY");
        if (playerCode == 1) {
            db.collection("users").document(uid).collection("notification").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            doc.getData().clear();
                        }
                    }

                }
            });
        }
    }
    /*private void getQuestionData() {
        if (questionModel == null) {
            // ini yang belum dapet
            getQuestionHandler.removeCallbacks(getQuestionRunnable);
            getQuestionHandler.post(getQuestionRunnable);
            getQuestionHandler.postDelayed(checkData, 1000);
            return;
        } else {
            // ini yang udah dapet
            getQuestionHandler.removeCallbacks(getQuestionRunnable);

        }
    }*/

    private void getQuestionList() {

        db.collection("room").document(ROOM_ID).collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    if (task.isSuccessful()) {
                        questionModel = doc.toObject(QuestionModel.class);
                        questionList.addAll(questionModel.getQuestionList());
                    }
                }
            }
        });
    }

//    private void intentToDrawActivity() {
//        Intent toDraw = new Intent(getContext(), MainActivity.class);
//        toDraw.putExtra("PLAYER_KEY", PLAYER_CODE);
//        toDraw.putExtra("ROOM_KEY", ROOM_ID);
//        startActivity(toDraw);
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Bundle bundle = getArguments();
        PLAYER_CODE = bundle.getInt("PLAYER_KEY");
        ROOM_ID = bundle.getString("ROOM_KEY");
        getQuestionList();
        deleteNotification();

        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                tv_countDown.setText(String.valueOf (millisUntilFinished / 1000));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
//                intentToDrawActivity();
            }

        }.start();
//        getQuestionHandler.post(setCountDown);

        /*getQuestionHandler.post(getQuestionRunnable);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (questionModel != null) {

                    getQuestionData();
                    intentToDrawActivity();
                } else {

                }
            }
        }).start();*/
    }
}
