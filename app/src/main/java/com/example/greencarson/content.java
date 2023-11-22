package com.example.greencarson;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class content extends AppCompatActivity{


    BottomNavigationView bottomNavigationView;
    NavigationFragment navigationFragment = new NavigationFragment();
    AddCenterFragment addCenterFragment = new AddCenterFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_content);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, navigationFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation){
                getSupportFragmentManager().beginTransaction().replace(R.id.container, navigationFragment).commit();
            } else if (itemId == R.id.addCenter){
                getSupportFragmentManager().beginTransaction().replace(R.id.container, addCenterFragment, "AddCenterFragment").commit();
            } else if (itemId == R.id.profile){
                getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
            }
            return true;
        });
    }
}