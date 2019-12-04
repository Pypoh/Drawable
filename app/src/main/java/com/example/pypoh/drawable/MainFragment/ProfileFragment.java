package com.example.pypoh.drawable.MainFragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private String mUser;
    private FirebaseFirestore db;
    private TextView name, level, battletag, tv_matches, tv_win, tv_loss;
    private Button btn_logout;
    private UserModel userModel;
    private CircleImageView civ_profile;
    private FirebaseStorage storage;
    public static final int PICK_IMAGE = 1;
    private Uri selectedImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        name = (TextView) view.findViewById(R.id.text_user_name);
        level = (TextView) view.findViewById(R.id.text_user_level);
        battletag = (TextView) view.findViewById(R.id.battletag);
        tv_matches = view.findViewById(R.id.totMatches);
        civ_profile = view.findViewById(R.id.civ_profile);
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
        civ_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        userModel = ((MainActivity) getActivity()).getUserModel();
        getMyData();
    }

    public void getMyData() {
        name.setText(userModel.getName());
        battletag.setText(userModel.getBattleTag());
        level.setText(String.valueOf(userModel.getLevel()));
        setLevel();
        if (userModel.getImage() != null) {
            Picasso.get().load(userModel.getImage()).into(civ_profile);
        }
        tv_matches.setText(String.valueOf(userModel.getMatches()));
        tv_win.setText(String.valueOf(userModel.getWin()));
        tv_loss.setText(String.valueOf(userModel.getLoss()));
    }

    private void toAuth() {
        Intent toAuthIntent = new Intent(getContext(), AuthActivity.class);
        startActivity(toAuthIntent);
        getActivity().finish();
    }

    private void setLevel() {
        level.setText(userModel.getBattleTag());
        if (userModel.getLevel() == 0){
            level.setText("Newbie");
        } else if (userModel.getLevel() > 5 && userModel.getLevel() <= 10){
            level.setText("Elite");
        } else if(userModel.getLevel() > 10 && userModel.getLevel() <= 15){
            level.setText("Pro");
        } else if(userModel.getLevel() > 15 && userModel.getLevel() <= 20){
            level.setText("Master");
        } else {
            level.setText("Developer");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE:
                    selectedImage = data.getData();
                    insertImage(selectedImage);
                    break;
            }
        }
    }

    public void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "pilih gambar"), PICK_IMAGE);
    }

    private void insertImage(Uri uri) {
        final StorageReference storageRef = storage.getReference().child(mUser);
        db.collection("users").document(mUser).update("image", userModel.getImage());
        UploadTask uploadTask = storageRef.putFile(uri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    userModel.setImage(downloadUri.toString());
                    db.collection("users").document(mUser).update("image", downloadUri.toString());
                    if (userModel.getImage() != null) {
                        Picasso.get().load(userModel.getImage()).into(civ_profile);
                    }
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

}
