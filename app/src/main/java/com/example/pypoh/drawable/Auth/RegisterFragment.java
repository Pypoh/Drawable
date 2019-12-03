package com.example.pypoh.drawable.Auth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pypoh.drawable.MainActivity;
import com.example.pypoh.drawable.Model.UserModel;
import com.example.pypoh.drawable.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Objects;
import java.util.Random;


public class RegisterFragment extends Fragment {

    TextView textDummyHintName;
    EditText editBattletag;
    TextView textDummyHintEmail;
    EditText editEmail;
    TextView textDummyHintPassword;
    EditText editPassword;
    TextView textDummyHintRePass;
    EditText editRePass;

    private String userId;
    private UserModel userModel;

    private Button signUpBtn;

    Handler getUserHandler = new Handler();

    Runnable getUserRunnable = new Runnable() {
        @Override
        public void run() {
            if (userModel == null && mAuth.getCurrentUser() != null) {
                getOnlineUser();
            }
        }
    };

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
        editBattletag = view.findViewById(R.id.edit_battletag);
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) { // Accept only letter & digits ; otherwise just return
                        Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }
        };
        editBattletag.setFilters(new InputFilter[]{filter});
        editBattletag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                    if (editBattletag.getText().length() > 0)
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
        if (TextUtils.isEmpty(editBattletag.getText().toString())) {
            Toast.makeText(getActivity(), "Please Enter Your Name...", Toast.LENGTH_SHORT).show();
            editBattletag.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), "Please Enter Your Email...", Toast.LENGTH_SHORT).show();
            editEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Please Enter Your Password...", Toast.LENGTH_SHORT).show();
            editPassword.requestFocus();
            return;
        }
        if (!password.equals(editRePass.getText().toString())) {
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
                            insertUserData(editBattletag.getText().toString(), editEmail.getText().toString());
                        } else {
                            signUpBtn.setEnabled(true);
                            task.getException();
                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void insertUserData(String battleTag, String email) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference userRef = db.collection("users").document(userId);
        Random random = new Random();
        String numBattleTag = String.valueOf(random.nextInt(9999));
        String name = battleTag;

        UserModel userModel = new UserModel(battleTag + "#" + numBattleTag, name, 0, 0, 0, 0, false);

        userRef.set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Inserted", Toast.LENGTH_SHORT).show();
                    getUserData();

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

    private void getOnlineUser() {
        userId = mAuth.getCurrentUser().getUid();
        Log.d("userIdDebug", userId + " ");

        DocumentReference userBattleTag = db.collection("users").document(userId);
        userBattleTag.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        userModel = documentSnapshot.toObject(UserModel.class);
                    }
                } else {
//                    Toast.makeText(MainActivity.this, "Failed get user data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUserData() {
        Runnable checkData = new Runnable() {
            @Override
            public void run() {
                getUserData();
            }
        };
        if (userModel == null) {
            // ini yang belum dapet
            getUserHandler.removeCallbacks(getUserRunnable);
            getUserHandler.post(getUserRunnable);
            getUserHandler.postDelayed(checkData, 1000);
            return;
        } else {
            // ini yang udah dapet
            getUserHandler.removeCallbacks(getUserRunnable);
            toMain();
        }

    }

    private void toMain() {
        Intent toMain = new Intent(getContext(), MainActivity.class);
        Gson objectData = new Gson();
        String userData = objectData.toJson(userModel);
        toMain.putExtra("USERDATA", userData);
        startActivity(toMain);
        getActivity().finish();
    }
}