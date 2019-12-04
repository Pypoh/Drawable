package com.example.pypoh.drawable.Matchmaking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.pypoh.drawable.Model.QuestionModel;
import com.example.pypoh.drawable.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MatchingActivity extends AppCompatActivity {

    public MatchingFragment matchingFragment = new MatchingFragment();
    public AcceptMatchingFragment acceptMatchingFragment = new AcceptMatchingFragment();

    public String opponentBattleTag;
    private int status;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
//    List<NotifModel> listNotification = new ArrayList<>();

    QuestionModel questionModel = new QuestionModel();

    ArrayList<String> questionFiltered = new ArrayList<>();

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
            setFragmentWithBundle(matchingFragment, bundle);
        } else {
            setFragmentWithBundle(matchingFragment, bundle);
        }
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        // tambahin delete notification
    }
}
