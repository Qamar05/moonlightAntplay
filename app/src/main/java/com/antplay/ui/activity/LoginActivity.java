package com.antplay.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class LoginActivity extends Activity implements View.OnClickListener {
    private String TAG = "ANT_PLAY";
    Button btnLetsGo;
    TextView tvForgetPass, tvSignupHere;
    EditText etEmail, etPass;
    String st_email, st_password;
    private ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvForgetPass = (TextView) findViewById(R.id.tv_forgetPass);
        tvSignupHere = (TextView) findViewById(R.id.tv_signupHere);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPass = (EditText) findViewById(R.id.et_password);
        btnLetsGo = (Button) findViewById(R.id.btn_signup);
        loadingPB = (ProgressBar) findViewById(R.id.loadingLogin_progress_xml);
        etEmail.setText("shobhit.agarwal@vmstechs.com");
        etPass.setText("Antplay@123");
        setOnClickListener();
    }

    private void setOnClickListener() {
        tvForgetPass.setOnClickListener(this);
        tvSignupHere.setOnClickListener(this);
        btnLetsGo.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_forgetPass:
                Intent intentForgotPassword = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intentForgotPassword);
                break;
            case R.id.tv_signupHere:
                Intent intentSignup = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intentSignup);
                break;
            case R.id.btn_signup:
                if (CheckAllLoginFields()) {
                    st_email = etEmail.getText().toString();
                    st_password = etPass.getText().toString();
                    callLoginAPi(st_email,st_password);
                }
                break;
        }
    }
    private boolean CheckAllLoginFields() {
        String emailPattern ="^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";
        if (etEmail.getText().toString().trim().length() == 0) {
            etEmail.setError(getString(R.string.error_email));
            return false;
        } else if (!etEmail.getText().toString().matches(emailPattern)) {
            etEmail.setError(getString(R.string.error_invalidEmail));
            return false;
        }
        if (etPass.getText().toString().trim().length() == 0) {
            etPass.setError(getString(R.string.error_password));
            return false;
        } else if (etPass.length() < 8) {
            etPass.setError(getString(R.string.error_pass_minimum));
            return false;
        }
        return true;
    }

    private void callLoginAPi(String stEmail,String strPassword) {
        loadingPB.setVisibility(View.VISIBLE);
        Log.d(TAG,"Email : "+stEmail+"Password : "+strPassword);
        LoginRequestModal loginRequestModal = new LoginRequestModal(stEmail,strPassword);
        RetrofitAPI retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        Call<ResponseBody> call = retrofitAPI.userLogin(loginRequestModal);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loadingPB.setVisibility(View.GONE);
                if (response != null) {
                    if (response.code() == 200) {
                        try {
                            String responseValue = response.body().string();
                            JSONObject jObj =  new JSONObject(responseValue);
                            String accessToken = jObj.getJSONObject("data").getString("access");
                            SharedPreferenceUtils.saveString(LoginActivity.this, Const.EMAIL_ID, jObj.getString("email"));
                            SharedPreferenceUtils.saveUserLoggedIn(LoginActivity.this, Const.IS_LOGGED_IN, true);
                            SharedPreferenceUtils.saveString(LoginActivity.this, Const.ACCESS_TOKEN, accessToken);
                            AppUtils.navigateScreen(LoginActivity.this, PcView.class);
                            finishAffinity();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == 401) {
                        try {
                            JSONObject jObj =  new JSONObject(response.errorBody().string());
                            String detailValue  =  jObj.getString("detail");
                            Log.d(TAG,"Issue : "+detailValue);
                            Toast.makeText(LoginActivity.this,  detailValue, Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Something went wrong, please try again later.", Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadingPB.setVisibility(View.GONE);
                Log.i("Error: ", ""+t.getMessage());
            }
        });
    }
}