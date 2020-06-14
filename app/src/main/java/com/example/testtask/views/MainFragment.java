package com.example.testtask.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.testtask.R;
import com.example.testtask.presenters.IPresenter;
import com.example.testtask.presenters.MyMainPresenter;

import java.util.concurrent.Executors;

import moxy.MvpAppCompatFragment;
import moxy.presenter.InjectPresenter;


public class MainFragment extends MvpAppCompatFragment implements IPresenter.IMainFragment {
    private BiometricPrompt mBiometricPromt;
    @InjectPresenter
    MyMainPresenter mMyMainPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBiometricPromt = new BiometricPrompt(this, Executors.newSingleThreadExecutor(), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Navigation.findNavController(view).navigate(R.id.menuFragment);
                mMyMainPresenter.messageShow();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().
                setTitle("Test Demo").
                setNegativeButtonText("Cancel").
                build();

        mBiometricPromt.authenticate(promptInfo);

    }

    @Override
    public void messages(int idR) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainFragment.this.getContext(),getString(idR),Toast.LENGTH_LONG).show();
            }
        });
    }
}
