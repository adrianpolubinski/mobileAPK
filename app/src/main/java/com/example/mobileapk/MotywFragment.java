package com.example.mobileapk;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MotywFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MotywFragment extends Fragment {

    private Button przelacz;
    SessionManager sessionManager;

    public MotywFragment() {
    }

    public static MotywFragment newInstance() {
        MotywFragment fragment = new MotywFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager((getActivity().getApplication().getApplicationContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_motyw, container, false);
        przelacz = v.findViewById(R.id.przelacz);
        if(Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            przelacz.setText("Motyw jasny");
        } else {
            przelacz.setText("Motyw ciemny");
        }

        przelacz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });


        return v;
    }
}