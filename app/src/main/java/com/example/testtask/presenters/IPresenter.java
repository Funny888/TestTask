package com.example.testtask.presenters;

import android.view.View;

import com.google.android.gms.fitness.FitnessOptions;

import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndSingleStrategy;
import moxy.viewstate.strategy.StateStrategyType;



public interface IPresenter{
    @StateStrategyType(AddToEndSingleStrategy.class)
    interface IMainFragment extends MvpView {
        void messages(int idR);
    }

    @StateStrategyType(AddToEndSingleStrategy.class)
    interface IDayActionFragment extends MvpView {
        void getWentSteps(int textWentSteps, int steps);
        void getFitnessOptions(FitnessOptions fitnessOptions);
    }
}
