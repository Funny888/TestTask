package com.example.testtask.views;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testtask.R;
import com.example.testtask.presenters.ActionForDayPresenter;
import com.example.testtask.presenters.IPresenter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.FitnessOptions;

import moxy.MvpAppCompatFragment;
import moxy.presenter.InjectPresenter;


public class ActionForToday extends MvpAppCompatFragment implements IPresenter.IDayActionFragment {
    private TextView mShowSteps;
    private static final int PERMISSION_ACTIVITY_RECOGNITION_CODE = 100;
    private static String TAG = "ActionForToday";

    @InjectPresenter
    ActionForDayPresenter mActionPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_action_for_today, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mShowSteps = view.findViewById(R.id.tv_day_action);
        requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION,"android.gms.permission.ACTIVITY_RECOGNITION",Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_ACTIVITY_RECOGNITION_CODE);
        mActionPresenter.getFitnessOptions(getContext());
    }


    @Override
    public void getWentSteps(int testSteps, int steps) {
        mShowSteps.setText(String.format(getString(testSteps), steps));
    }

    @Override
    public void getFitnessOptions(FitnessOptions fitnessOptions) {
        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(getContext(),
                fitnessOptions);
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    getActivity(),
                    PERMISSION_ACTIVITY_RECOGNITION_CODE,
                    account,
                    fitnessOptions);
        } else {
            mActionPresenter.getWentSteps();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PERMISSION_ACTIVITY_RECOGNITION_CODE) {
                mActionPresenter.getWentSteps();
            }
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
