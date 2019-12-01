package com.example.pypoh.drawable;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pypoh.drawable.Adapter.FriendListAdapter;
import com.example.pypoh.drawable.Matchmaking.MatchingActivity;
import com.example.pypoh.drawable.Model.FriendModel;
import com.example.pypoh.drawable.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment {

    public static FriendListAdapter mAdapter;
    private Button btn_add;
    private EditText et_battleTag, et_search_name;
    private ImageView iv_close, iv_add_friend;
    private TextView tv_nama, tv_battletag, tv_matches, tv_win, tv_loss;
    private Button btn_remove, btn_add_to_battle;
    private FirebaseAuth auth;
    private StorageReference imageStorage;
    private FirebaseFirestore db;
    private ImageView bt_checker;
    private RecyclerView recyclerView;
    private FriendModel friendModel;
    private UserModel userModel;
    private String friendUid;

    private List<FriendModel> friendsData = new ArrayList<>();

    public FriendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        imageStorage = FirebaseStorage.getInstance().getReference();
        initViews(view);
        recyclerView = view.findViewById(R.id.recycler_friend);

        et_search_name = view.findViewById(R.id.search_friend);
        et_search_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                String nama = et_search_name.getText().toString();
                if (!charSequence.equals("")) {
                    searchName(charSequence.toString());
                } else {
                    mAdapter.setFriend(friendsData);
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void getData() {
        String uid = auth.getCurrentUser().getUid();
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
                        FriendModel friendModel = documentSnapshot.toObject(FriendModel.class);
                        friendsData.add(friendModel);
                    }
                    mAdapter.notifyDataSetChanged();
//                    System.out.println("Current data: " + queryDocumentSnapshots.getData());
                } else {
                    System.out.print("Current data: null");
                }
            }
        });
    }

    private void initViews(View view) {
        initViewComponents(view);
    }

    private void initCustomDialog() {
        final Dialog customDialog = new Dialog(getContext());
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.dialog_add_friend);
        Window window = customDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setCancelable(false);

        bt_checker = customDialog.findViewById(R.id.bt_checker);

        btn_add = customDialog.findViewById(R.id.btn_add_friend);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertFriendData(customDialog);

            }
        });

        et_battleTag = customDialog.findViewById(R.id.et_battle_tag);
        et_battleTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bt_checker.setVisibility(View.INVISIBLE);
                if (!charSequence.equals("")) {
                    searchBattleTag(charSequence.toString());
                    Log.d("IDFriendB", charSequence.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        iv_close = customDialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_battleTag.setText("");
                bt_checker.setVisibility(View.INVISIBLE);
                customDialog.dismiss();
            }
        });
        customDialog.show();

    }

    private void initViewComponents(View view) {
        iv_add_friend = view.findViewById(R.id.iv_addFriend);
        iv_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCustomDialog();
            }
        });

    }

    private void initProfileDialog(final FriendModel friendModel) {
        final Dialog customDialog = new Dialog(getContext());
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.dialog_view_profile);
        Window window = customDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setCancelable(true);

        tv_battletag = customDialog.findViewById(R.id.tv_bt_profile);
        tv_battletag.setText(friendModel.getBattletag());
        tv_nama = customDialog.findViewById(R.id.tv_nama_profile);
        tv_nama.setText(friendModel.getName());
        tv_matches = customDialog.findViewById(R.id.tv_profile_matches);
        tv_matches.setText(String.valueOf(friendModel.getMatches()));
        tv_win = customDialog.findViewById(R.id.tv_profile_win);
        tv_win.setText(String.valueOf(friendModel.getWin()));
        tv_loss = customDialog.findViewById(R.id.tv_profile_loses);
        tv_loss.setText(String.valueOf(friendModel.getLoss()));
        btn_remove = customDialog.findViewById(R.id.remove_friend);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFriendData(friendModel);
                customDialog.dismiss();
            }
        });
        btn_add_to_battle = customDialog.findViewById(R.id.add_to_battle);
        btn_add_to_battle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(enabledBtn) {
                    btn_add_to_battle.setEnabled(true);
                    String battleTag = friendModel.getBattletag();
                    Intent toMatching = new Intent(getContext(), MatchingActivity.class);
                    toMatching.putExtra("BATTLE_TAG_KEY", battleTag);
                    getContext().startActivity(toMatching);
                    customDialog.dismiss();
                }*/
                getDataOnline(friendModel.getBattletag());
                /*else {
                    Toast.makeText(getContext(), friendModel.getName() + " sedang offline", Toast.LENGTH_SHORT).show();
                }*/

            }
        });
        customDialog.show();
    }

    public void searchName(String nama) {
        List<FriendModel> friends = new ArrayList<>();
        for (FriendModel friend : friendsData) {
            if (friend.getName().contains(nama)) {
                friends.add(friend);
//                FriendListAdapter list = new FriendListAdapter(getContext(), friends);
//                list.setOnItemClickListener(new FriendListAdapter.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(FriendModel friendModel) {
//                        initProfileDialog(friendModel);
//                    }
//                });
//                recyclerView.setAdapter(list);
            }

        }
        mAdapter.setFriend(friends);
        mAdapter.notifyDataSetChanged();
    }

    private void searchBattleTag(final String battleTag) {
        Log.d("UserBattleTag", " " + battleTag);
//        Log.d("friendsData", " "+ friendsData.get(0).getBattletag());
        final String userId = auth.getCurrentUser().getUid();
        final Handler handler;
//        friendsData.clear();
        db.collection("users").whereEqualTo("battleTag", battleTag).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    friendUid = doc.getId();
                    Log.d("IDFriendBattleTag", " " + friendUid);
                    Log.d("IDUser", " " + userId);
//                    Log.d("")
                    if (!userId.equals(friendUid)) {
                        if (friendsData.isEmpty()) {
                            friendModel = doc.toObject(FriendModel.class);
                            bt_checker.setVisibility(View.VISIBLE);
                            btn_add.setEnabled(true);
                        } else {
                            for (FriendModel friend : friendsData) {
                                if (!battleTag.equals(friend.getBattletag())) {
                                    friendModel = doc.toObject(FriendModel.class);
                                    bt_checker.setVisibility(View.VISIBLE);
                                    btn_add.setEnabled(true);
                                } else {
                                    bt_checker.setVisibility(View.INVISIBLE);
                                    btn_add.setEnabled(false);
                                    Toast.makeText(getContext(), friend.getName() + " Telah berteman", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } else {
                        friendModel = doc.toObject(FriendModel.class);
                        bt_checker.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Datamu Sendiri", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                bt_checker.setVisibility(View.INVISIBLE);
            }
        });

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        getMyData();
        getData();
        mAdapter = new FriendListAdapter(getContext(), friendsData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter.setOnItemClickListener(new FriendListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FriendModel friendModel) {
                initProfileDialog(friendModel);
            }
        });
        recyclerView.setAdapter(mAdapter);

    }

    private void getMyData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                userModel = doc.toObject(UserModel.class);
            }
        });
    }

    private void deleteFriendData(FriendModel friendModel) {
        try {
            final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d("insertFriendID", friendUid + "");
            String battleTag = friendModel.getBattletag();
            db.collection("users").whereEqualTo("battleTag", battleTag).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        String friendUid = doc.getId();
                        db.collection("users").document(userId).collection("friend").document(friendUid).delete();
                        db.collection("users").document(friendUid).collection("friend").document(userId).delete();
                    }
                }
            });

        } catch (Exception e) {
            Log.e("failedToDelete", e.getMessage());
        }

    }

    private void insertFriendData(Dialog customDialog) {
        try {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d("insertFriendID", friendUid + "");
            db.collection("users").document(userId).collection("friend").document(friendUid).set(friendModel);
            db.collection("users").document(friendUid).collection("friend").document(userId).set(userModel);
            customDialog.dismiss();
            et_battleTag.setText("");
            bt_checker.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Berhasil menambahkan teman", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("failedToInsert", e.getMessage());
        }

    }

    private void getDataOnline(final String battleTag) {
        final String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("friend").whereEqualTo("battleTag", battleTag).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    friendUid = doc.getId();
                    Log.d("friendid", friendUid);
                    if (task.isSuccessful()) {
                        CollectionReference friendRef = db.collection("online-users");
                        friendRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    System.err.println("Listen failed: " + e);
                                    return;
                                }
                                if (queryDocumentSnapshots != null) {
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                        if (friendUid.equals(documentSnapshot.getId())) {
                                            Intent toMatching = new Intent(getContext(), MatchingActivity.class);
                                            toMatching.putExtra("BATTLE_TAG_KEY", battleTag);
                                            getContext().startActivity(toMatching);
                                        } else {
                                            Toast.makeText(getContext(), friendModel.getName() + " sedang offline", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    System.out.print("Current data: null");
                                }
                            }
                        });
                    }


                }
            }
        });
    }

}
