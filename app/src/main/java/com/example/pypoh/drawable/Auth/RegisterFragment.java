package com.example.pypoh.drawable.Auth;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pypoh.drawable.Model.UserModel;
import com.example.pypoh.drawable.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class RegisterFragment extends Fragment {

    TextView textDummyHintName;
    EditText editName;
    TextView textDummyHintEmail;
    EditText editEmail;
    TextView textDummyHintPassword;
    EditText editPassword;
    TextView textDummyHintRePass;
    EditText editRePass;

    private Button signUpBtn;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        signUpBtn = view.findViewById(R.id.button_sign_up_register);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                signUpBtn.setEnabled(false);
                signUp(editEmail.getText().toString(), editPassword.getText().toString());
            }
        });


        textDummyHintName = view.findViewById(R.id.text_dummy_hint_name);
        editName = view.findViewById(R.id.edit_name);
        editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Show white background behind floating label
                            textDummyHintName.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                } else {
                    // Required to show/hide white background behind floating label during focus change
                    if (editName.getText().length() > 0)
                        textDummyHintName.setVisibility(View.VISIBLE);
                    else
                        textDummyHintName.setVisibility(View.INVISIBLE);
                }
            }
        });

        textDummyHintEmail = view.findViewById(R.id.text_dummy_hint_email);
        editEmail = view.findViewById(R.id.edit_email);
        editEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Show white background behind floating label
                            textDummyHintEmail.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                } else {
                    // Required to show/hide white background behind floating label during focus change
                    if (editEmail.getText().length() > 0)
                        textDummyHintEmail.setVisibility(View.VISIBLE);
                    else
                        textDummyHintEmail.setVisibility(View.INVISIBLE);
                }
            }
        });

        textDummyHintPassword = view.findViewById(R.id.text_dummy_hint_password);
        editPassword = view.findViewById(R.id.edit_password);
        editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Show white background behind floating label
                            textDummyHintPassword.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                } else {
                    // Required to show/hide white background behind floating label during focus change
                    if (editPassword.getText().length() > 0)
                        textDummyHintPassword.setVisibility(View.VISIBLE);
                    else
                        textDummyHintPassword.setVisibility(View.INVISIBLE);
                }
            }
        });

        textDummyHintRePass = view.findViewById(R.id.text_dummy_hint_repassword);
        editRePass = view.findViewById(R.id.edit_repassword);
        editRePass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Show white background behind floating label
                            textDummyHintRePass.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                } else {
                    // Required to show/hide white background behind floating label during focus change
                    if (editRePass.getText().length() > 0)
                        textDummyHintRePass.setVisibility(View.VISIBLE);
                    else
                        textDummyHintRePass.setVisibility(View.INVISIBLE);
                }
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void signUp(String email, String password) {
        if(TextUtils.isEmpty(editName.getText().toString())){
            Toast.makeText(getActivity(), "Please Enter Your Name...", Toast.LENGTH_SHORT).show();
            editName.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getActivity(), "Please Enter Your Email...", Toast.LENGTH_SHORT).show();
            editEmail.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(getActivity(), "Please Enter Your Password...", Toast.LENGTH_SHORT).show();
            editPassword.requestFocus();
            return;
        }
        if(!password.equals(editRePass.getText().toString())){
            Toast.makeText(getActivity(), "Password Not Matching...", Toast.LENGTH_SHORT).show();
            editRePass.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            insertUserData(editName.getText().toString(), editEmail.getText().toString());
                        } else {
                            signUpBtn.setEnabled(true);
                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void insertUserData(String name, String email) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        UserModel userModel = new UserModel(userId, name, email, 0);

        userRef.set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Inserted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Fail Insert", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
}
