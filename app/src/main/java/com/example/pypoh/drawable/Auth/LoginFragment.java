package com.example.pypoh.drawable.Auth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.example.pypoh.drawable.MainActivity;
import com.example.pypoh.drawable.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    TextView textDummyHintPassword;
    EditText editPassword;
    TextView textDummyHintEmail;
    EditText editEmail;

    private Button loginBtn;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginBtn = view.findViewById(R.id.button_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserEmailPass(editEmail.getText().toString().trim(), editPassword.getText().toString().trim());
                loginBtn.setEnabled(false);
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


        return view;
    }

    private void loginUserEmailPass(String email, String password) {
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getActivity(),"Please Enter Your Email", Toast.LENGTH_SHORT).show();
            editEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(getActivity(), "Please Enter Your Password", Toast.LENGTH_SHORT).show();
            editPassword.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    toMain();
                } else {
                    Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                    loginBtn.setEnabled(true);

                }
            }
        });
    }

    private void toMain() {
        Intent toMain = new Intent(getContext(), MainActivity.class);
        startActivity(toMain);
        getActivity().finish();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }
}
