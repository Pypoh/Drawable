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
import android.widget.TextView;
import android.widget.Toast;

import com.example.pypoh.drawable.Model.UserModel;
import com.example.pypoh.drawable.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AcceptMatchingFragment extends Fragment {

    String uidInvited;
    Bundle bundle = getArguments();
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    private UserModel userModel;

    private TextView tv_host, tv_opponent;
    private CircleImageView civ_host, civ_opponent;

    private String json, json2, name, image;
    private Gson gson, gson2;

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

        civ_host = view.findViewById(R.id.civ_host);
        civ_opponent = view.findViewById(R.id.civ_opponent);
        tv_host = view.findViewById(R.id.tv_host);
        tv_opponent = view.findViewById(R.id.tv_opponent);

//        if (bundle != null) {
//            int intentCode = bundle.getInt("STATUS_KEY");
//            switch (intentCode) {
//                case 0:
//                    json = bundle.getString("PROFILE_DATA");
//                    name = bundle.getString("NAME_FRIEND_KEY");
//                    image = bundle.getString("PROFILE_PICTURE_FRIEND_KEY");
//                    gson = new Gson();
//                    userModel = gson.fromJson(json, UserModel.class);
//                    if (name != null) {
//                        tv_opponent.setText(name);
//                    }
//                    if (image != null) {
//                        Picasso.get().load(image).into(civ_opponent);
//                    }
//                    if (userModel.getImage() != null) {
//                        Picasso.get().load(userModel.getImage()).into(civ_host);
//                    }
//                    if (userModel.getName() != null) {
//                        tv_host.setText(userModel.getName());
//                    }
//                    break;
//
//                case 1:
//
//                    json2 = bundle.getString("PROFILE_DATA");
//                    gson2 = new Gson();
//                    userModel = gson2.fromJson(json2, UserModel.class);
//                    if (userModel.getImage() != null) {
//                        Picasso.get().load(userModel.getImage()).into(civ_opponent);
//                    }
//                    if (userModel.getName() != null) {
//                        tv_opponent.setText(userModel.getName());
//                    }
//                    break;
//
//                // Inflate the layout for this fragment
//
//            }
//        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        deleteNotification();
        final Bundle bundle = getArguments();
        if (bundle != null) {
            String nameHost = bundle.getString("HOST_NAME_KEY");
            String imageHost = bundle.getString("HOST_IMAGE_KEY");
            String nameOpponent = bundle.getString("OPPONENT_NAME_KEY");
            String imageOpponent = bundle.getString("OPPONENT_IMAGE_KEY");

            try {
                tv_host.setText(nameHost);
                Picasso.get().load(imageHost).into(civ_host);
                tv_opponent.setText(nameOpponent);
                Picasso.get().load(imageOpponent).into(civ_opponent);
            } catch (Exception e) {

            }
            uidInvited = bundle.getString("FRIEND_ID");

            // load data


//            bundle.putString();
            new Handler().postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    ((MatchingActivity) Objects.requireNonNull(getActivity())).setFragmentWithBundle(new SelectQuestionFragment(), bundle);

                }
            }, 2000);
        } else {
            Toast.makeText(getContext(), "Unable to get questions", Toast.LENGTH_SHORT).show();
        }
    }

//    private void deleteNotification() {
//        String uid = mAuth.getCurrentUser().getUid();
//        db.collection("users").document(uidInvited).collection("notification").document(uid).delete();
//    }
}
