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
            checkStatus(status);
        } else {
            checkStatus(status);
        }
    }

    private void checkStatus(int status) {
        setFragment(matchingFragment, status);
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

//    private void divideQuestion(final String roomId) {
//        String uId = mAuth.getCurrentUser().getUid();
//        db.collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                for (DocumentSnapshot documentSnapshot : task.getResult()) {
//                    if (task.isSuccessful()) {
//                        ArrayList<String> a = new ArrayList<>();
//                        questionModel = documentSnapshot.toObject(QuestionModel.class);
//                        if (questionModel != null) {
//                            a.addAll(questionModel.getQuestionList());
//                        }
//                        Collections.shuffle(a);
//                        questionModel.setQuestionList(a);
//                    }
//                }
//                questionFiltered.clear();
//                for (int i = 0; i < 18; i++) {
//                    // disini dia ga dapet question modelnya
//                    questionFiltered.add(questionModel.getQuestionList().get(i));
//                }
//                db.collection("room").document(roomId).update("availableQuestion", questionFiltered).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d("question", questionModel.getQuestionList().get(0));
//                        }
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MatchingActivity.this, "Tidak ada data", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
