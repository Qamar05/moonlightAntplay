package com.antplay.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.antplay.R;
import com.antplay.utils.Const;

public class LoginSignUpActivity extends Activity {
    Button btnLogin, btnRegister, btnGoogleLogin, btn_otp;

    TextView tv_tandC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);
        btnGoogleLogin = (Button) findViewById(R.id.continueWithGoogle);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btn_otp = (Button) findViewById(R.id.continueWithOTP);
        btnRegister = (Button) findViewById(R.id.btn_register);
        tv_tandC = (TextView) findViewById(R.id.tv_termsandc_login);

        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent i = new Intent(LoginSignUpActivity.this, LoginWithOTP.class);
                startActivity(i);*/
            }
        });

        tv_tandC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(LoginActivity.this, TermsAndCondition.class);
                startActivity(intent);*/

                Intent intent = new Intent(LoginSignUpActivity.this, GeneralWebViewActivity.class);
                intent.putExtra(Const.REDIRECT_URL,Const.TERMS_AND_CONDITION_URL);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginSignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginSignUpActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}