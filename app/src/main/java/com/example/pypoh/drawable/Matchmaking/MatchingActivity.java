package com.example.pypoh.drawable.Matchmaking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.pypoh.drawable.AcceptMatchingFragment;
import com.example.pypoh.drawable.MatchingFragment;
import com.example.pypoh.drawable.Model.NotifModel;
import com.example.pypoh.drawable.Model.QuestionModel;
import com.example.pypoh.drawable.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchingActivity extends AppCompatActivity {

    public MatchingFragment matchingFragment = new MatchingFragment();
    public AcceptMatchingFragment acceptMatchingFragment = new AcceptMatchingFragment();

    public String opponentBattleTag;
    private int status;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    List<NotifModel> listNotification = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            opponentBattleTag = bundle.getString("BATTLE_TAG_KEY");
            status = bundle.getInt("STATUS_KEY");
            checkStatus(status);
        } else {
            checkStatus(status);
        }
    }

    private void checkStatus(int status) {
        switch (status) {
            case 0:
                setFragment(matchingFragment, status);
                break;
            case 1:
                setFragment(matchingFragment, status);
                // ubah data di firestore
                updateNotificationData();

        }
    }

    private void updateNotificationData() {
        final String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).collection("notification").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        NotifModel notifModel1 = doc.toObject(NotifModel.class);
                        if (notifModel1 != null) {
                            if (notifModel1.getStatus() == 2) {
                                // Intent ke aktipiiti sebelah;
                                setFragment(acceptMatchingFragment, 0);
                            } else if (notifModel1.getStatus() == 1) {
                                // nanti send notif juga
                                // intent ke main
                            }
                        }
                    }

                }
            }
        });
        db.collection("users").document(uid).collection("notification").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        listNotification.add(doc.toObject(NotifModel.class));
                    }
                    try {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                db.collection("users").document(uid).collection("notification").document(listNotification.get(0).getBattleTag()).update("status", 2);
                            }
                        }, 2000);
                    } catch (Exception e) {

                    }
                }
            }
        });
    }


    public String getOpponentBattleTag() {
        return opponentBattleTag;
    }

    public void setOpponentBattleTag(String opponentBattleTag) {
        this.opponentBattleTag = opponentBattleTag;
    }

    public void setFragment(Fragment fragment, int args) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.matching_main_frame, fragment, "MAIN_FRAGMENT");
        Bundle bundle = new Bundle();
        bundle.putInt("STATUS_KEY", args);
        fragment.setArguments(bundle);
//        ft.addToBackStack(null);
        ft.commit();
    }

    public void setFragmentWithBundle(Fragment fragment, Bundle bundle) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.matching_main_frame, fragment, "MAIN_FRAGMENT");
        fragment.setArguments(bundle);
//        ft.addToBackStack(null);
        ft.commit();
    }
}
