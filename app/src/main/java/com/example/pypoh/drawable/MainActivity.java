package com.example.pypoh.drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.pypoh.drawable.MainFragment.BattleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // Fragments
    private BattleFragment battleFragment = new BattleFragment();

    // Utils
    boolean doubleBackToExitPressedOnce = false;

    private Menu bottomMenu;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_battle:
                    setFragment(battleFragment);
//                    changeIconStateBar(R.id.navigation_adventure, R.drawable.navbar_lesson_on);
                    return true;
//                case R.id.navigation_friend:
//                    setFragment(playFragment);
////                    changeIconStateBar(R.id.navigation_pronounciation, R.drawable.navbar_play_on);
//                    return true;
//                case R.id.navigation_profile:
//                    setFragment(battleFragment);
////                    changeIconStateBar(R.id.navigation_multiplayer, R.drawable.navbar_battle_on);
//                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        bottomMenu = navView.getMenu();

        setFragment(battleFragment);

    }

//    private void changeIconStateBar(int pathItem, int pathIcon) {
//        bottomMenu.findItem(R.id.navigation_adventure).setIcon(R.drawable.navbar_lesson_off);
//        bottomMenu.findItem(R.id.navigation_pronounciation).setIcon(R.drawable.navbar_play_off);
//        bottomMenu.findItem(R.id.navigation_multiplayer).setIcon(R.drawable.navbar_battle_off);
//        bottomMenu.findItem(R.id.navigation_profile).setIcon(R.drawable.navbar_profile_off);
//
//        bottomMenu.findItem(pathItem).setIcon(pathIcon);
//    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, fragment, "MAIN_FRAGMENT");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // TODO: On Back Pressed When Last Fragment
    }
}
