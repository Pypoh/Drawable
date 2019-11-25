package com.example.pypoh.drawable.Auth;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.pypoh.drawable.R;

import java.util.Objects;

public class AuthFragment extends Fragment {

    private Button _btnLogin, _btnRegist, _btnGuest;

    public AuthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auth, container, false);

        _btnLogin = view.findViewById(R.id.auth_login_button);
        _btnRegist = view.findViewById(R.id.auth_regist_button);
        _btnGuest = view.findViewById(R.id.auth_guest_button);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                changeFragment(new LoginFragment());
            }
        });

        _btnRegist.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                changeFragment(new RegisterFragment());
            }
        });

        _btnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _btnGuest.setEnabled(false);
//                Intent playAsGuest = new Intent(getContext(), EducationLevel.class);
//                playAsGuest.putExtra("AuthCode", "3");
//                startActivity(playAsGuest);
                Toast.makeText(getContext(), "Under Development", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void changeFragment(Fragment fragment) {
        ((AuthActivity) Objects.requireNonNull(getActivity())).setSecondFragment(fragment);
    }
}
