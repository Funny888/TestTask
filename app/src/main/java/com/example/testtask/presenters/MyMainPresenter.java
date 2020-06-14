package com.example.testtask.presenters;

import com.example.testtask.R;

import moxy.InjectViewState;
import moxy.MvpPresenter;

@InjectViewState
public class MyMainPresenter extends MvpPresenter<IPresenter.IMainFragment> {
    public void messageShow() {
        getViewState().messages(R.string.menuFragment);
    }

}
