package com.example.pypoh.drawable.MainFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pypoh.drawable.Auth.AuthActivity;
import com.example.pypoh.drawable.MainActivity;
import com.example.pypoh.drawable.Model.UserModel;
import com.example.pypoh.drawable.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private TextView name, level, battletag, tv_matches, tv_win, tv_loss;
    private Button btn_logout;
    private UserModel userModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        name = (TextView) view.findViewById(R.id.text_user_name);
        level = (TextView) view.findViewById(R.id.text_user_level);
        battletag = (TextView) view.findViewById(R.id.battletag);
        tv_matches = view.findViewById(R.id.totMatches);
        tv_win = view.findViewById(R.id.totWin);
        tv_loss = view.findViewById(R.id.totLos);
        btn_logout = view.findViewById(R.id.button_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                toAuth();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userModel = ((MainActivity) getActivity()).getUserModel();
        getMyData();
    }

    public void getMyData() {
        name.setText(userModel.getName());
        level.setText(String.valueOf(userModel.getLevel()));
        battletag.setText(userModel.getBattleTag());
        tv_matches.setText(String.valueOf(userModel.getMatches()));
        tv_win.setText(String.valueOf(userModel.getWin()));
        tv_loss.setText(String.valueOf(userModel.getLoss()));
    }

    private void toAuth() {
        Intent toAuthIntent = new Intent(getContext(), AuthActivity.class);
        startActivity(toAuthIntent);
        getActivity().finish();
    }

}
