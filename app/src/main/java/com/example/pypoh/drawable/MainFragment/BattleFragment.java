package com.example.pypoh.drawable.MainFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pypoh.drawable.Adapter.FriendListAdapter;
import com.example.pypoh.drawable.Adapter.ModeAdapter;
import com.example.pypoh.drawable.MainActivity;
import com.example.pypoh.drawable.Matchmaking.MatchingActivity;
import com.example.pypoh.drawable.Model.FriendModel;
import com.example.pypoh.drawable.Model.ModeModel;
import com.example.pypoh.drawable.Model.UserModel;
import com.example.pypoh.drawable.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.circleindicator.CircleIndicator2;

public class BattleFragment extends Fragment {

    private RecyclerView recyclerViewMode;
    private ModeAdapter modeAdapter;

    private List<ModeModel> modeData = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<FriendModel> friendsData = new ArrayList<>();
    private FriendListAdapter mAdapter;
    private TextView username, level;
    private UserModel userModel;
    private CircleImageView civ_profile;
    private ImageView level_icon;

    // Circle Indicator Utils
    PagerSnapHelper pagerSnapHelper;
    CircleIndicator2 indicator;

    // Dialog Utils
    Dialog opponentDialog;
    Dialog friendListDialog;

    // Adapter

    public BattleFragment() {
        // Required empty public constructor
    }

    public BattleFragment(FriendModel friendModel) {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_battle, container, false);

        if (modeData.isEmpty()) {
            addModeData();
        }

        // Setup Views
        recyclerViewMode = view.findViewById(R.id.recycler_mode);

        pagerSnapHelper = new PagerSnapHelper();

        indicator = view.findViewById(R.id.modes_indicator);

        username = view.findViewById(R.id.text_user_name);
        level = view.findViewById(R.id.text_user_level);
        civ_profile = view.findViewById(R.id.civ_profile);
        level_icon = view.findViewById(R.id.level_icon);


        return view;
    }

    private void addModeData() {
        modeData.add(new ModeModel(0, "1 VS 1", "Mode ini memungkinkan pemain melawan satu sama lain"));
        modeData.add(new ModeModel(1, "2 VS 2", "Mode ini memungkinkan pemain bertarung dua lawan dua"));
        modeData.add(new ModeModel(2, "Group", "Mode ini memungkinkan pemain bermain bersama maksimal 4 orang"));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userModel = ((MainActivity) (getActivity())).getUserModel();


        // Setup RecyclerView
        recyclerViewMode.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        modeAdapter = new ModeAdapter(getContext(), modeData);
        modeAdapter.setOnItemClickListener(new ModeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ModeModel modeModel) {
                if (modeModel.getMode() == 0) {
                    showOpponentDialog();
                } else {
                    Toast.makeText(getContext(), "Under Development", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerViewMode.setAdapter(modeAdapter);

        pagerSnapHelper.attachToRecyclerView(recyclerViewMode);
        indicator.attachToRecyclerView(recyclerViewMode, pagerSnapHelper);

        setView();


    }

    private void setView() {
        username.setText(userModel.getBattleTag());
        if (userModel.getLevel() == 0) {
            level.setText("Newbie");
            level_icon.setImageResource(R.drawable.pangkat_1);
        } else if (userModel.getLevel() > 5 && userModel.getLevel() <= 10) {
            level.setText("Elite");
            level_icon.setImageResource(R.drawable.pangkat_2);
        } else if (userModel.getLevel() > 10 && userModel.getLevel() <= 15) {
            level.setText("Pro");
            level_icon.setImageResource(R.drawable.pangkat_3);
        } else if (userModel.getLevel() > 15 && userModel.getLevel() <= 20) {
            level.setText("Master");
            level_icon.setImageResource(R.drawable.crowns);
        } else {
            level.setText("Developer");
            level_icon.setImageResource(R.drawable.java);
        }
        if (userModel.getImage() != null) {
            Picasso.get().load(userModel.getImage()).into(civ_profile);
        }
    }

    private void showOpponentDialog() {
        opponentDialog = new Dialog(getContext());
        opponentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        opponentDialog.setContentView(R.layout.dialog_opponent);
        opponentDialog.setCancelable(true);

        Button buttonRandomOpponent = opponentDialog.findViewById(R.id.button_battle_random);
        buttonRandomOpponent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Under Development", Toast.LENGTH_SHORT).show();
            }
        });
        Button buttonFriendOpponent = opponentDialog.findViewById(R.id.button_battle_friend);
        buttonFriendOpponent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opponentDialog.dismiss();
                inviteFriendDialog();
            }
        });

        opponentDialog.show();
        Window window = opponentDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        opponentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void inviteFriendDialog() {
        friendListDialog = new Dialog(getContext());
        friendListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        friendListDialog.setContentView(R.layout.dialog_add_to_battle);
        friendListDialog.setCancelable(true);
        RecyclerView recyclerViewFriends = friendListDialog.findViewById(R.id.recycler_battle_friend);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        FriendListAdapter friendListAdapter = new FriendListAdapter(getContext(), friendsData);
        recyclerViewFriends.setAdapter(friendListAdapter);
        getData(friendListAdapter);
        friendListAdapter.setOnItemClickListener(new FriendListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FriendModel friendModel) {
                Intent toMatching = new Intent(getContext(), MatchingActivity.class);
                toMatching.putExtra("BATTLE_TAG_KEY", friendModel.getBattleTag());
                toMatching.putExtra("PROFILE_PICTURE_FRIEND_KEY", friendModel.getImage());
                toMatching.putExtra("NAME_FRIEND_KEY", friendModel.getName());
                //add gson
                Gson gson = new Gson();
                String json = gson.toJson(userModel);
                toMatching.putExtra("PROFILE_DATA", json);

                getActivity().startActivity(toMatching);
            }
        });
        friendListDialog.show();
        Window window = friendListDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        friendListDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void getData(final FriendListAdapter adapter) {
        String uid = mAuth.getCurrentUser().getUid();
        CollectionReference friendRef = db.collection("users").document(uid).collection("friend");
        friendRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("Listen failed: " + e);
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    // Disini tambah data
                    friendsData.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        //Mengirim dan Memasukkan Semua Objek ke friendModel
                        FriendModel friendModel = documentSnapshot.toObject(FriendModel.class);
                        if (friendModel.isOnline()) {
                            friendsData.add(friendModel);
                            Log.d("testDebugFriendList", friendModel.getName());
                        }
                    }
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
//                    System.out.println("Current data: " + queryDocumentSnapshots.getData());
                } else {
                    System.out.print("Current data: null");
                }
            }
        });
    }
}
