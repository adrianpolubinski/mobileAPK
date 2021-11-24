package com.example.mobileapk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class LoggedActivity extends AppCompatActivity {

    SessionManager sessionManager;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        sessionManager = new SessionManager(this);


        fragmentManager = getSupportFragmentManager();
        try {
            fragmentManager.beginTransaction().replace(R.id.frameLayout, AllUsersFragment.class.newInstance()).commit();

            getSupportActionBar().setTitle("Użytkownicy");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}