package com.example.pypoh.drawable;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.pypoh.drawable.Adapter.FriendListAdapter;
import com.example.pypoh.drawable.Model.FriendModel;
import com.example.pypoh.drawable.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendFragment extends Fragment {

    private Dialog customDialog;
    private Button btn_add;
    private EditText et_battleTag;
    private ImageView iv_close, iv_add_friend;

    public TextView tv_nama, tv_rank;
    public Boolean online_indicator;

    private FirebaseAuth auth;
    private StorageReference imageStorage;
    private FirebaseFirestore db;
    private ImageView bt_checker;
    private RecyclerView recyclerView;
    public static FriendListAdapter mAdapter;
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
        initCustomDialog();
        initViewComponents(view);
    }

    private void initCustomDialog() {
        customDialog = new Dialog(getContext());
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
                insertFriendData();
                customDialog.dismiss();
                et_battleTag.setText("");
                bt_checker.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Berhasil menambahkan teman", Toast.LENGTH_SHORT).show();
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
                String battleTag = et_battleTag.getText().toString();
                searchBattleTag(battleTag);
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

    }

    private void initViewComponents(View view) {
        iv_add_friend = view.findViewById(R.id.iv_addFriend);
        iv_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.show();
            }
        });


    }

    private void searchBattleTag(String battleTag) {
        db.collection("users").whereEqualTo("battleTag", battleTag).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    friendUid = doc.getId();
                    friendModel = doc.toObject(FriendModel.class);
                    bt_checker.setVisibility(View.VISIBLE);
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
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

    private void insertFriendData() {
        try {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d("insertFriendID", friendUid + "");
            db.collection("users").document(userId).collection("friend").document(friendUid).set(friendModel);
            db.collection("users").document(friendUid).collection("friend").document(userId).set(userModel);
        } catch (Exception e) {
            // apakek
        }

    }
}
