package com.antplay.ui.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.ImageReader;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.antplay.Game;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.LoginRequestModal;
import com.antplay.models.Payment;
import com.antplay.utils.AppUtils;
import com.google.gson.Gson;
import com.antplay.models.LoginResponse;
import com.antplay.utils.Const;
import com.antplay.utils.RestClient;
import com.antplay.utils.SharedPreferenceUtils;
import com.antplay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "ANT_PLAY";
    Button btnLetsGo;
    TextView tvForgetPass, tvSignupHere;
    EditText etEmail, etPass;
    ImageView ivPasswordShow;
    String st_email, st_password;
    private ProgressBar loadingPB;
    RetrofitAPI retrofitAPI;
    Context mContext;
    boolean showPassword =  false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext   = LoginActivity.this;
        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        tvForgetPass = (TextView) findViewById(R.id.tv_forgetPass);
        ivPasswordShow =  findViewById(R.id.ivPasswordShow);
        tvSignupHere = (TextView) findViewById(R.id.tv_signupHere);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPass = (EditText) findViewById(R.id.et_password);
        btnLetsGo = (Button) findViewById(R.id.btn_signup);
        loadingPB = (ProgressBar) findViewById(R.id.loadingLogin_progress_xml);

        tvForgetPass.setOnClickListener(this);
        tvSignupHere.setOnClickListener(this);
        btnLetsGo.setOnClickListener(this);
        ivPasswordShow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_forgetPass:
                AppUtils.navigateScreenWithoutFinish(LoginActivity.this, ForgotPasswordActivity.class);
                break;
            case R.id.tv_signupHere:
                AppUtils.navigateScreenWithoutFinish(LoginActivity.this, SignupActivity.class);
                break;
            case R.id.btn_signup:
                if (CheckAllLoginFields()) {
                    if (AppUtils.isOnline(mContext))
                        callLoginAPi(etEmail.getText().toString(), etPass.getText().toString());
                    else
                        AppUtils.showInternetDialog(mContext);
                }
                break;
            case R.id.ivPasswordShow:
                showHidePassword();
                break;
        }
    }

    private boolean CheckAllLoginFields() {
        String emailPattern ="^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";
        if (etEmail.getText().toString().trim().length() == 0) {
            etEmail.setError(getString(R.string.error_email));
            etEmail.requestFocus();
            return false;
        } else if (!etEmail.getText().toString().matches(emailPattern)) {
            etEmail.setError(getString(R.string.error_invalidEmail));
            etEmail.requestFocus();
            return false;
        }
        if (etPass.getText().toString().trim().length() == 0) {
            etPass.setError(getString(R.string.error_password));
            etPass.requestFocus();
            return false;
        } else if (etPass.length() < 8) {
            etPass.setError(getString(R.string.error_pass_minimum));
            etPass.requestFocus();
            return false;
        }
//        else if (!etPass.getText().toString().matches(Const.passwordRegex)) {
//            etPass.setError(getString(R.string.pass_regex));
//            etPass.requestFocus();
//            return false;
//        }
        return true;
    }

    private void callLoginAPi(String stEmail,String strPassword) {
        loadingPB.setVisibility(View.VISIBLE);
        LoginRequestModal loginRequestModal = new LoginRequestModal(stEmail,strPassword);
        Call<ResponseBody> call = retrofitAPI.userLogin(loginRequestModal);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loadingPB.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        try {
                            String responseValue = response.body().string();
                            JSONObject jObj = new JSONObject(responseValue);
                            String accessToken = jObj.getJSONObject("data").getString("access");
                            String loginEmail = jObj.getString("email");
                            SharedPreferenceUtils.saveUserLoggedIn(LoginActivity.this, Const.IS_LOGGED_IN, true);
                            SharedPreferenceUtils.saveString(LoginActivity.this, Const.ACCESS_TOKEN, accessToken);
                            SharedPreferenceUtils.saveString(LoginActivity.this, Const.LOGIN_EMAIL, loginEmail);
                            SharedPreferenceUtils.saveString(LoginActivity.this,Const.SHOW_OVERLAY,"false");
                            AppUtils.navigateScreen(LoginActivity.this, PcView.class);
                            finishAffinity();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == Const.ERROR_CODE_400 ||
                                response.code()==Const.ERROR_CODE_500||
                                response.code()==Const.ERROR_CODE_404 ||
                            response.code()==401) {
                        try {
                            JSONObject jObj = new JSONObject(response.errorBody().string());
                            Toast.makeText(LoginActivity.this, jObj.getString("detail"), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Something went wrong, please try again later.", Toast.LENGTH_LONG).show();
                    }
                }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadingPB.setVisibility(View.GONE);
                Log.i("Error: ", "" + t.getMessage());
            }
        });
    }
    private void showHidePassword() {
        if(!showPassword) {
            if (etPass.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ivPasswordShow.setImageResource(R.drawable.visibile_icon);
                //Show Password
                etPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showPassword = true;
            }
        }
        else{
            ivPasswordShow.setImageResource(R.drawable.visibility_off);
            //Hide Password
            etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            showPassword = false;
        }
    }


}