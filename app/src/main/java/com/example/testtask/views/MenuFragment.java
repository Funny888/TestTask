package com.example.testtask.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.testtask.R;

import moxy.MvpAppCompatFragment;


public class MenuFragment extends MvpAppCompatFragment implements View.OnClickListener {

    private static final int PERMISSION_ACTIVITY_RECOGNITION_CODE = 100;

    private Button mActionToday;
    private Button mActionWeekly;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActionToday = view.findViewById(R.id.btn_actionToday);
        mActionToday.setOnClickListener(this);
        mActionWeekly = view.findViewById(R.id.btn_chart_action);
        mActionWeekly.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        NavController navigation = Navigation.findNavController(this.getView());
        switch (v.getId()) {
            case R.id.btn_actionToday: {
                navigation.navigate(R.id.actionForToday);
                break;
            }
            case R.id.btn_chart_action: {
                navigation.navigate(R.id.chart_action);
                break;
            }
        }
    }
}
