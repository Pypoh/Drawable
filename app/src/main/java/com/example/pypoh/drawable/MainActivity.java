package com.example.pypoh.drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.pypoh.drawable.MainFragment.BattleFragment;
import com.example.pypoh.drawable.Model.FriendModel;
import com.example.pypoh.drawable.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Fragments
    private BattleFragment battleFragment = new BattleFragment();
    private FriendFragment friendFragment = new FriendFragment();

    // Firebase Utils
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private String userId;
    public static ArrayList<FriendModel> friend;

    // Utils
    boolean doubleBackToExitPressedOnce = false;

    private Menu bottomMenu;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_battle:
                    setFragment(battleFragment);
//                    changeIconStateBar(R.id.navigation_adventure, R.drawable.navbar_lesson_on);
                    return true;
                case R.id.navigation_friend:
                    setFragment(friendFragment);
//                    changeIconStateBar(R.id.navigation_pronounciation, R.drawable.navbar_play_on);
                    return true;
//                case R.id.navigation_profile:
//                    setFragment(battleFragment);
////                    changeIconStateBar(R.id.navigation_multiplayer, R.drawable.navbar_battle_on);
//                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();

        bottomMenu = navView.getMenu();

        setFragment(battleFragment);

    }

//    private void changeIconStateBar(int pathItem, int pathIcon) {
//        bottomMenu.findItem(R.id.navigation_adventure).setIcon(R.drawable.navbar_lesson_off);
//        bottomMenu.findItem(R.id.navigation_pronounciation).setIcon(R.drawable.navbar_play_off);
//        bottomMenu.findItem(R.id.navigation_multiplayer).setIcon(R.drawable.navbar_battle_off);
//        bottomMenu.findItem(R.id.navigation_profile).setIcon(R.drawable.navbar_profile_off);
//
//        bottomMenu.findItem(pathItem).setIcon(pathIcon);
//    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, fragment, "MAIN_FRAGMENT");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // TODO: On Back Pressed When Last Fragment
    }

    @Override
    protected void onPause() {
        super.onPause();

        setOfflineUser();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setOnlineUser();
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
                    Toast.makeText(MainActivity.this, "Failed get user data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setOfflineUser() {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference onlineUserRef = db.collection("online-users").document(userId);
        onlineUserRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("setOfflineUser", "User Offline!");
                }
            }
        });

    }

    /*private void setOnline(String userId, String get_battleTag) {
        DocumentReference onlineRef = db.collection("online-users").document(userId);
        OnlineUser onlineUser = new OnlineUser(get_battleTag);
        onlineRef.set(onlineUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }*/
}
