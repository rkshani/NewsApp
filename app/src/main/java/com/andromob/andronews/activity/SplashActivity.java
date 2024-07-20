package com.andromob.andronews.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.andromob.andronews.R;
import com.andromob.andronews.utils.Methods;
import com.andromob.andronews.utils.Prefs;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    Methods methods;
    Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        methods = new Methods(this);
        prefs = Prefs.getInstance(this);
        runTask();
    }

    private void runTask() {
        if (methods.isNetworkAvailable()) {
            if (!prefs.isAppCheckDone()) {
                methods.startThread(this);
            } else {
                methods.getAppSettings(SplashActivity.this);
            }
        } else {
            methods.errorDialog(getString(R.string.something_went_wrong), getString(R.string.check_internet));
        }
    }
}
