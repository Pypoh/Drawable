package com.example.pypoh.drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.pypoh.drawable.Auth.AuthActivity;
import com.example.pypoh.drawable.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Intent intent;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (mAuth.getCurrentUser() != null){
                    mUser = mAuth.getCurrentUser();
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    setOnlineUser();
                }else {
                    intent = new Intent(getApplicationContext(), AuthActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }).start();
    }

    private void setOnlineUser() {
        userId = mAuth.getCurrentUser().getUid();
        Log.d("userIdDebug", userId + " ");

        DocumentReference userBattleTag = db.collection("users").document(userId);
        userBattleTag.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        UserModel userModel = documentSnapshot.toObject(UserModel.class);
                        DocumentReference userRef = db.collection("online-users").document(userId);
                        if (userModel != null) {
                            userRef.set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("setOnlineSuccess", "User online!");
                                    }
                                }
                            });
                        } else {
                            Log.d("setOnlineError", "Failed to find user");
                        }
                    }
                } else {
                    Toast.makeText(SplashScreenActivity.this, "Failed get user data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
