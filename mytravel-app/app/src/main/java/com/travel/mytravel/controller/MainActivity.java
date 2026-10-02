package com.travel.mytravel.controller;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.travel.mytravel.R;
import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.controller.fragment.BudgetFragment;
import com.travel.mytravel.controller.fragment.CreateTripFragment;
import com.travel.mytravel.controller.fragment.HomeFragment;
import com.travel.mytravel.controller.fragment.PlanFragment;
import com.travel.mytravel.controller.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiClient.init(getApplicationContext());

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_plan) {
                selectedFragment = new CreateTripFragment();
            } else if (itemId == R.id.nav_add) {
                selectedFragment = new PlanFragment();
            } else if (itemId == R.id.nav_budget) {
                selectedFragment = new BudgetFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }

    public void selectTab(int navItemId) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(navItemId);
        }
    }
}
