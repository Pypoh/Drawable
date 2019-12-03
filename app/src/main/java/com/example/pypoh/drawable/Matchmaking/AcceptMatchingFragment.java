package com.example.pypoh.drawable.Matchmaking;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pypoh.drawable.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AcceptMatchingFragment extends Fragment {

    String uidInvited;
    Bundle bundle = getArguments();
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public AcceptMatchingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accept_matching, container, false);
        // Inflate the layout for this fragment
        if(bundle != null){

        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        deleteNotification();
        final Bundle bundle = getArguments();
        if (bundle!=null) {
            uidInvited = bundle.getString("FRIEND_ID");
            new Handler().postDelayed(new Runnable() {

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    ((MatchingActivity) Objects.requireNonNull(getActivity())).setFragmentWithBundle(new SelectQuestionFragment(), bundle);
                }
            },2000);
        } else {
            Toast.makeText(getContext(), "Unable to get questions", Toast.LENGTH_SHORT).show();
        }
    }

//    private void deleteNotification() {
//        String uid = mAuth.getCurrentUser().getUid();
//        db.collection("users").document(uidInvited).collection("notification").document(uid).delete();
//    }
}
