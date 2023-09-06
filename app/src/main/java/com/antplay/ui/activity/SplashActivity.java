package com.antplay.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;

import com.antplay.R;
import com.antplay.ui.intrface.ClearService;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.SharedPreferenceUtils;

public class SplashActivity extends Activity {
    private String TAG = "ANT_PLAY";
    boolean isNotFirstTime,isAlreadyLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        isNotFirstTime=  SharedPreferenceUtils.getBoolean(SplashActivity.this,Const.IS_FIRST_TIME);
        isAlreadyLogin =  SharedPreferenceUtils.getBoolean(SplashActivity.this, Const.IS_LOGGED_IN);
        //doSomethingAfterAppKilled();

        new Handler().postDelayed(() -> {
            Intent i;
            if (isAlreadyLogin)
               AppUtils.navigateScreen(SplashActivity.this, PcView.class);
            else if(isNotFirstTime)
                AppUtils.navigateScreen(SplashActivity.this, LoginSignUpActivity.class);
            else
                AppUtils.navigateScreen(SplashActivity.this, OnBoardingActivity.class);

        }, 4000);
    }

    private void doSomethingAfterAppKilled() {
        new Handler().postDelayed(() -> {
            Log.i("testt_killed_app "  , " tetstststt");
        }, 4000);
    }
    }
