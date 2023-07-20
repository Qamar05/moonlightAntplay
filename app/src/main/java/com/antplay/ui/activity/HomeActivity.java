package com.antplay.ui.activity;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.antplay.R;
import com.antplay.ui.fragments.HomeFragment;
import com.antplay.ui.fragments.SettingsFragment;
import com.antplay.ui.fragments.VMFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener {
    Fragment homeFragment;
    VMFragment vmFragment;
    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeFragment = new HomeFragment();
        vmFragment = new VMFragment();
        settingsFragment = new SettingsFragment();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.person:
          /*      getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, new HomeFragment(),"")
                        .commit();*/
                return true;

            case R.id.home:
              /*  getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, vmFragment)
                        .commit();*/
                return true;

            case R.id.settings:
               /* getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, settingsFragment)
                        .commit();*/
                return true;
        }
        return false;
    }
}