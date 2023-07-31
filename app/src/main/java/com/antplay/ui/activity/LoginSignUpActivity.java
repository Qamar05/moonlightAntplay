package com.antplay.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.antplay.R;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;

public class LoginSignUpActivity extends Activity implements View.OnClickListener {
    Button btnLogin, btnRegister, btnGoogleLogin, btn_otp;
    Context mContext;
    TextView tv_tandC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);
        mContext =  LoginSignUpActivity.this;
        btnGoogleLogin = (Button) findViewById(R.id.continueWithGoogle);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btn_otp = (Button) findViewById(R.id.continueWithOTP);
        btnRegister = (Button) findViewById(R.id.btn_register);
        tv_tandC = (TextView) findViewById(R.id.tv_termsandc_login);


        btn_otp.setOnClickListener(this);
        tv_tandC.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.continueWithOTP:
                AppUtils.navigateScreenWithoutFinish((Activity)mContext, LoginWithOTP.class);
                break;
            case R.id.tv_termsandc_login:
                AppUtils.navigateScreenSendValue((Activity) mContext, GeneralWebViewActivity.class,Const.REDIRECT_URL,Const.TERMS_AND_CONDITION_URL);
                break;
            case R.id.btn_login:
                AppUtils.navigateScreenWithoutFinish((Activity) mContext, LoginActivity.class);
                break;
            case R.id.btn_register:
                AppUtils.navigateScreenWithoutFinish((Activity) mContext, SignupActivity.class);
                break;
        }
    }
}