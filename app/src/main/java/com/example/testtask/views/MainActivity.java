package com.example.testtask.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.example.testtask.R;

public class MainActivity extends AppCompatActivity {
    NavController mNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigation = Navigation.findNavController(this,R.id.nav_frag);
    }
}
