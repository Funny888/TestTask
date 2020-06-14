package com.example.testtask.presenters;

import android.content.Context;

import com.example.testtask.R;
import com.example.testtask.models.FetchWentSteps;

import moxy.InjectViewState;
import moxy.MvpPresenter;

@InjectViewState
public class ActionForDayPresenter extends MvpPresenter<IPresenter.IDayActionFragment> implements IResult {
    private FetchWentSteps mFetchWentSteps;

    public void getWentSteps() {
        mFetchWentSteps.findSensors();
    }

    public void getFitnessOptions(Context context) {
        mFetchWentSteps = new FetchWentSteps(context);
        mFetchWentSteps.registerListener(this);
        getViewState().getFitnessOptions(mFetchWentSteps.fitnessOptions());
    }


    @Override
    public void result(int steps) {
        getViewState().getWentSteps(R.string.went_steps, steps);
    }
}
