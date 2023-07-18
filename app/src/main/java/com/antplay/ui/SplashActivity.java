package com.antplay.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;

import com.antplay.PcView;
import com.antplay.R;
import com.antplay.utils.Const;
import com.antplay.utils.SharedPreferenceUtils;

public class SplashActivity extends Activity {
    private String TAG = "ANT_PLAY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        boolean isNotFirstTime=  SharedPreferenceUtils.getBoolean(SplashActivity.this,Const.IS_FIRST_TIME);

        new Handler().postDelayed(() -> {
            Intent i;
            if (SharedPreferenceUtils.getBoolean(SplashActivity.this, Const.IS_LOGGED_IN)){
                Log.e(TAG,"Logged in :"+SharedPreferenceUtils.getBoolean(SplashActivity.this, Const.IS_LOGGED_IN));
                i = new Intent(SplashActivity.this, PcView.class);
                startActivity(i);
            }
            else if(isNotFirstTime){
                // This method will be execute once the timer is over
                i = new Intent(SplashActivity.this, LoginActivity.class);
            }else {
                // This method will be execute once the timer is over
               // i = new Intent(SplashActivity.this, OnBoardingActivity.class);
                i = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(i);
            finish();
        }, 4000);
    }
}