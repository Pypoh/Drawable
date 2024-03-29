package com.example.pypoh.drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.pypoh.drawable.Drawing.DrawingFragment;
import com.example.pypoh.drawable.MainFragment.BattleFragment;
import com.example.pypoh.drawable.MainFragment.FriendFragment;
import com.example.pypoh.drawable.MainFragment.ProfileFragment;
import com.example.pypoh.drawable.Matchmaking.MatchingActivity;
import com.example.pypoh.drawable.Model.FriendModel;
import com.example.pypoh.drawable.Model.NotifModel;
import com.example.pypoh.drawable.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Fragments
    private BattleFragment battleFragment = new BattleFragment();
    private FriendFragment friendFragment = new FriendFragment();
    private ProfileFragment profileFragment = new ProfileFragment();

    // Firebase Utils
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private String userId, json;
    public static ArrayList<FriendModel> friend;

    // Utils
    boolean doubleBackToExitPressedOnce = false;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private NotificationManager mNotificationManager;

    private UserModel userModel;

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    private List<String> allFriend = new ArrayList<>();


    private Menu bottomMenu;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_battle:
                    setFragment(battleFragment);
                    changeIconStateBar(R.id.navigation_battle, R.drawable.navbar_battle_on);
                    return true;
                case R.id.navigation_friend:
                    setFragment(friendFragment);
                    changeIconStateBar(R.id.navigation_friend, R.drawable.navbar_multiplayer_on);
                    return true;
                case R.id.navigation_profile:
                    setFragment(profileFragment);
                    changeIconStateBar(R.id.navigation_profile, R.drawable.navbar_profile_on);
//                    setFragment(new DrawingFragment());
                    return true;
            }
            return false;
        }
    };

    private void changeIconStateBar(int item, int icon) {
        bottomMenu.findItem(R.id.navigation_battle).setIcon(R.drawable.navbar_battle_off);
        bottomMenu.findItem(R.id.navigation_friend).setIcon(R.drawable.navbar_multiplayer_off);
        bottomMenu.findItem(R.id.navigation_profile).setIcon(R.drawable.navbar_profile_off);

        bottomMenu.findItem(item).setIcon(icon);
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFERENCES", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        json = gson.toJson(userModel);
        editor.putString("UserModel", json);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFERENCES", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserModel", null);
        userModel = gson.fromJson(json, UserModel.class);
    }

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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson objectJson = new Gson();
            userModel = objectJson.fromJson(bundle.getString("USERDATA"), UserModel.class);
            Log.d("getDataUserDebug", userModel.getName());
            saveData();
        } else {
            loadData();
        }

        checkNotification();
        setOnlineUser();
        tellOthersThatImOnline();

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
        saveData();

        if (mAuth.getCurrentUser() != null) {
            tellOthersThatImOffline();
            setOfflineUser();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth != null && db != null) {
            setOnlineUser();
            tellOthersThatImOnline();
        }
    }

    private void setOnlineUser() {
        userId = mAuth.getCurrentUser().getUid();
        Log.d("userIdDebug", userId + " ");

        final DocumentReference userBattleTag = db.collection("users").document(userId);
        userBattleTag.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        if (userModel == null) {
                            userModel = documentSnapshot.toObject(UserModel.class);
                            setUserModel(userModel);
                        }
                        DocumentReference userRef = db.collection("online-users").document(userId);
                        if (userModel != null) {
                            userRef.set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("setOnlineSuccess", "User online!");
                                        userBattleTag.update("online", true);
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
        final String userId = mAuth.getCurrentUser().getUid();
        DocumentReference onlineUserRef = db.collection("online-users").document(userId);
        onlineUserRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("setOfflineUser", "User Offline!");
                    db.collection("users").document(userId).update("online", false);
                }
            }
        });

    }

    private void tellOthersThatImOnline() {
        final String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).collection("friend").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                allFriend.clear();
                for (DocumentSnapshot doc : task.getResult()) {
                    String friendId = doc.getId();
                    Log.d("userOnlineDebugLagi", friendId);
                    allFriend.add(friendId);
                }
                // get data from users
                for (String id : allFriend) {
                    Log.d("userOnlineDebugs", id + " : " + uid);
                    db.collection("users").document(id).collection("friend").document(uid).update("online", true);
                }
            }
        });


    }

    private void tellOthersThatImOffline() {
        final String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).collection("friend").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    String friendId = doc.getId();
                    Log.d("userOnlineDebugLagi", friendId);
                    allFriend.add(friendId);
                }
                // get data from users
                for (String id : allFriend) {
                    Log.d("userOnlineDebugs", id + " : " + uid);
                    db.collection("users").document(id).collection("friend").document(uid).update("online", false);
                }
            }
        });


    }

    private void checkNotification() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).collection("notification").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        NotifModel notifModel = documentSnapshot.toObject(NotifModel.class);
                        if (notifModel != null) {
                            if (notifModel.getStatus() == 0) {
                                pushNotification();
                            }
                        }
                        /*FriendModel friendModel = documentSnapshot.toObject(FriendModel.class);
                        if (friendModel.isOnline()) {
                            friendsData.add(friendModel);
                            Log.d("testDebugFriendList", friendModel.getName());
                        }*/
                    }
                } else {
//                    Log.d("notificationDebug", "Notification Deleted");
//                    mNotificationManager.cancelAll();
                }
            }
        });
    }

    private void pushNotification() {
        Intent resultIntent = new Intent(this.getApplicationContext(), MatchingActivity.class);
        resultIntent.putExtra("STATUS_KEY", 1);
        resultIntent.putExtra("PROFILE_DATA", json);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_mail_black_24dp);
        mBuilder.setContentTitle("Battle Invitation")
                .setContentText("Are you ready?")
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .addAction(R.drawable.ic_check_black_24dp, "Accept", resultPendingIntent);
        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.YELLOW);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mNotificationManager.cancelAll();
            }
        }, 9000);
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
