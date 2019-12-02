package com.example.pypoh.drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.pypoh.drawable.Auth.AuthActivity;
import com.example.pypoh.drawable.Model.FriendModel;
import com.example.pypoh.drawable.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private String userId;
    private List<String> allFriend = new ArrayList<>();

    private UserModel userModel;

    Handler getUserHandler = new Handler();

    Runnable getUserRunnable = new Runnable() {
        @Override
        public void run() {
            if (userModel == null && mAuth.getCurrentUser() != null) {
                getOnlineUser();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getUserHandler.post(getUserRunnable);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mAuth.getCurrentUser() != null) {
                    mUser = mAuth.getCurrentUser();
//                    setOnlineUser();
                    getUserData();
                } else {
                    toAuth();
                }

            }
        }).start();
    }

    private void toAuth() {
        Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
        startActivity(intent);
        finish();
    }

    private void toMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Gson objectData = new Gson();
        String userData = objectData.toJson(userModel);
        intent.putExtra("USERDATA", userData);
        startActivity(intent);
        finish();
    }

    private void getUserData() {
        Runnable checkData = new Runnable() {
            @Override
            public void run() {
                getUserData();
            }
        };
        if (userModel == null) {
            // ini yang belum dapet
            getUserHandler.removeCallbacks(getUserRunnable);
            getUserHandler.post(getUserRunnable);
            getUserHandler.postDelayed(checkData, 1000);
            return;
        } else {
            // ini yang udah dapet
            getUserHandler.removeCallbacks(getUserRunnable);
            toMain();
        }
    }

    private void getOnlineUser() {
        userId = mAuth.getCurrentUser().getUid();
        Log.d("userIdDebug", userId + " ");

        DocumentReference userBattleTag = db.collection("users").document(userId);
        userBattleTag.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        userModel = documentSnapshot.toObject(UserModel.class);
                    }
                } else {
//                    Toast.makeText(MainActivity.this, "Failed get user data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
