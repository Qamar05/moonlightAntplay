package com.antplay.ui.activity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.antplay.AppView;
import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.SendOTPResponse;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginWithOTP extends AppCompatActivity implements View.OnClickListener {

    EditText edt_phone;
    Button btn_lestGo;

    boolean isPhoneNumberEntered = false;
    ProgressBar progressBar;
    TextView tv_register;
    Context mContext;
    String phoneValue;
    RetrofitAPI retrofitAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_with_otp);
        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        mContext = LoginWithOTP.this;
        progressBar = findViewById(R.id.progress_sendOTP);
        edt_phone = findViewById(R.id.et_phone_otp);
        btn_lestGo = findViewById(R.id.btn_letsGo_otp);
        tv_register = findViewById(R.id.tv_signupHere_loginWithOTP);
        tv_register.setOnClickListener(this);
        btn_lestGo.setOnClickListener(this);
    }

    private void callSendOTP() {
        progressBar.setVisibility(View.VISIBLE);
        Call<SendOTPResponse> call = retrofitAPI.sendOTP(Const.DEV_URL+"getuserbyphone/"+phoneValue);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SendOTPResponse> call, Response<SendOTPResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.code() == Const.SUCCESS_CODE_200) {
                    AppUtils.navigateScreenSendValue(LoginWithOTP.this, VerifyOTP.class, "mobile", phoneValue);
                } else if (response.code() == Const.ERROR_CODE_404) {
                    Toast.makeText(mContext, getString(R.string.enter_registered_mobile), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SendOTPResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private boolean checkPhoneNumberEntered() {
        if (edt_phone.length() == 0) {
            edt_phone.setError(getString(R.string.error_phone));
            return false;
        } else if (!edt_phone.getText().toString().matches(Const.phoneRegex)) {
            edt_phone.setError(getString(R.string.error_invalidPhone));
            return false;
        }
        return true;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_signupHere_loginWithOTP:
                AppUtils.navigateScreen(LoginWithOTP.this, SignupActivity.class);
                break;
            case R.id.btn_letsGo_otp:
                phoneValue = edt_phone.getText().toString();
                if (checkPhoneNumberEntered()) {
                    if (AppUtils.isOnline(mContext))
                        callSendOTP();
                    else
                        AppUtils.showInternetDialog(mContext);
                }
                break;

        }
    }
}